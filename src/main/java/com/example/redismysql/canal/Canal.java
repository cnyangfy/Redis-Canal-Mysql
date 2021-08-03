package com.example.redismysql.canal;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class Canal{

//    @Autowired
//    private RedisTemplate redisTemplate;

    public void run(RedisTemplate redisTemplate) {
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("localhost",
                11111), "example", "", "");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            int totalEmptyCount = 120;
            while (emptyCount < totalEmptyCount) {
                Message message = connector.getWithoutAck(batchSize); //
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                } else {
                    emptyCount = 0;
                    printEntry(message.getEntries(),redisTemplate);
                }

                connector.ack(batchId); //
                // connector.rollback(batchId); //
            }

        } finally {
            connector.disconnect();
        }
    }


    public void printEntry(List<CanalEntry.Entry> entrys,RedisTemplate redisTemplate) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            CanalEntry.EventType eventType = rowChage.getEventType();
            System.out.println(eventType);

            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    redisDelete(entry.getHeader().getTableName(),rowData.getBeforeColumnsList(),redisTemplate);
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    System.out.println(1);
                    redisInsert(entry.getHeader().getTableName(),rowData.getAfterColumnsList(),redisTemplate);
                } else if (eventType == CanalEntry.EventType.UPDATE){
                    redisUpdate(entry.getHeader().getTableName(),rowData.getAfterColumnsList(),redisTemplate);
                }
            }
        }
    }
    private void redisDelete(String tableName, List<CanalEntry.Column> columns,RedisTemplate redisTemplate) {
        JSONObject json = new JSONObject();
        for (CanalEntry.Column column : columns) {
            json.put(column.getName(), column.getValue());
        }

        if (columns.size() > 0) {
            try {
                redisTemplate.delete(tableName + ":" + columns.get(0).getValue());
            }catch (Exception e){
                System.out.println("Delete error: "+e);
            }
        }
    }

    private void redisInsert(String tableName, List<CanalEntry.Column> columns,RedisTemplate redisTemplate) {
        JSONObject json = new JSONObject();
        for (CanalEntry.Column column : columns) {
            json.put(column.getName(), column.getValue());
        }
            if (columns.size() > 0) {
                try {
                    redisTemplate.opsForValue().set(tableName + ":" + columns.get(0).getValue(), json.toJSONString(), 120, TimeUnit.SECONDS);
                }catch (Exception e){
                    System.out.println("Insert error: "+e);
            }
        }
    }

    private void redisUpdate(String tableName, List<CanalEntry.Column> columns,RedisTemplate redisTemplate) {
        JSONObject json = new JSONObject();
        for (CanalEntry.Column column : columns) {
            json.put(column.getName(), column.getValue());
        }
        if (columns.size() > 0) {
            try {
                redisTemplate.delete(tableName + ":" + columns.get(0).getValue());
                redisTemplate.opsForValue().set(tableName + ":" + columns.get(0).getValue(), json.toJSONString(), 120, TimeUnit.SECONDS);
            }catch (Exception e){
                System.out.println("Update error: "+e);
            }
        }
    }

}
