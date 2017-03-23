package jh.test.storm.simple;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * 接收喷发节点(Spout)发送的数据进行简单的处理后，发射出去。
 *
 * 重写方法execute和declareOutputFields，
 * 这个类就是用于执行具体的作业，准确的说是execute方法用来执行相关的计算 
 * 
 * @author jh
 * 
 */
@SuppressWarnings("serial")
public class SimpleBolt extends BaseBasicBolt {
 
    public void execute(Tuple input, BasicOutputCollector collector) {
        try {
            String msg = input.getString(0);
            if (msg != null){
                System.out.println("msg="+msg);
                collector.emit(new Values(msg + "msg is processed!"));
            }
                 
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
 
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("info"));
    }
 
}