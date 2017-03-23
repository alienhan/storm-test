/*
 * Copyright (C), 2002-2017, 金融公司
 * FileName: HelloTopology.java
 * Author:   16111263
 * Date:     2017-3-22 上午09:39:14
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package jh.test.storm.hello;

/**
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author 16111263
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class HelloTopology {

	public static class HelloSpout extends BaseRichSpout {

		/**
		     */
		private static final long serialVersionUID = 8680218632589197172L;
		boolean isDistributed;
		SpoutOutputCollector collector;

		public HelloSpout() {
			this(true);
		}

		public HelloSpout(boolean isDistributed) {
			this.isDistributed = isDistributed;
		}

		@SuppressWarnings("rawtypes")
		public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
			this.collector = collector;
		}

		public void close() {
		}

		public void nextTuple() {
			Utils.sleep(100);
			final String[] words = new String[] { "china", "usa", "japan", "russia", "england" };
			final Random rand = new Random();
			final String word = words[rand.nextInt(words.length)];
			this.collector.emit(new Values(word));
		}

		public void ack(Object msgId) {
		}

		public void fail(Object msgId) {
		}

		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

		@Override
		public Map<String, Object> getComponentConfiguration() {
			if (!this.isDistributed) {
				Map<String, Object> ret = new TreeMap<String, Object>();
				ret.put(Config.TOPOLOGY_MAX_TASK_PARALLELISM, 1);
				return ret;
			} else {
				return null;
			}
		}
	}

	public static class HelloBolt extends BaseRichBolt {
		/**
		     */
		private static final long serialVersionUID = -4976659786251419108L;
		OutputCollector collector;

		@SuppressWarnings("rawtypes")
		@Override
		public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
			this.collector = collector;
		}

		@Override
		public void execute(Tuple tuple) {
			this.collector.emit(tuple, new Values("hello," + tuple.getString(0)));
			this.collector.ack(tuple);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}
	}

	public static void main(String[] args) throws Exception {

		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("a", new HelloSpout(), 10);
		builder.setBolt("b", new HelloBolt(), 5).shuffleGrouping("a");

		Config conf = new Config();
		conf.setDebug(true);

		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);
			//StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
		} else {
			String test_id = "hello_test";
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(test_id, conf, builder.createTopology());
			Utils.sleep(10000);
			cluster.killTopology(test_id);
			cluster.shutdown();
		}
	}
}
