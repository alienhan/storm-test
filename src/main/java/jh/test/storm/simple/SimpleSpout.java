package jh.test.storm.simple;


import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
 
/**
 * 首先建立SimpleSpout类作为数据源，并且继承于父类BaseRichSpout，
 * 确定后可以看到系统自动补全3个方法：nextTuple,open和declareOutputFields
 * 
 * open方法是数据源的初始化，
 * nextTuple的作用是把Tuple发送至下游，
 * declareOutputFields用来定义输出字段
 * 
 * Spout起到和外界沟通的作用，他可以从一个数据库中按照某种规则取数据，也可以从分布式队列中取任务
 * 
 * @author jh
 *
 */
@SuppressWarnings("serial")
public class SimpleSpout extends BaseRichSpout{
    //用来发射数据的工具类
    private SpoutOutputCollector collector;
    private static String[] info = new String[]{
    	"jh","hanhan","wwbb"
    };
     
    Random random=new Random();
     

    /**
     * 初始化collector
     */
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }
     
    /**
     * 在SpoutTracker类中被调用，每调用一次就可以向storm集群中发射一条数据（一个tuple元组），该方法会被不停的调用
     */
    @Override
    public void nextTuple() {
        try {
            String msg = info[2];
            // 调用发射方法
            collector.emit(new Values(msg));
            // 模拟等待100ms
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 定义字段id，该id在简单模式下没有用处，但在按照字段分组的模式下有很大的用处。
     * 该declarer变量有很大作用，我们还可以调用declarer.declareStream();来定义stramId，该id可以用来定义更加复杂的流拓扑结构
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("source")); //collector.emit(new Values(msg));参数要对应
    }
 
}
