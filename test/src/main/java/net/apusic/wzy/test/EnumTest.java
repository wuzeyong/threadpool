package net.apusic.wzy.test;

public enum EnumTest {
    LOGIN("登录管理控制平台"),START_NODE("启动节点"),STOP_NODE("停止节点"),
    MODIFY_SYSTEM_PARAM("修改系统参数"),MODIFY_PASSWORD("修改密码"),
    CREATE_TASK("创建任务"),EDIT_TASK("编辑任务"),DELETE_TASK("删除任务"),
    START_TASK("启动任务"),STOP_TASK(" 停止任务"),CREATE_NODE("创建节点"),
    EDIT_NODE("编辑节点"),DELETE_NODE("删除节点"),CREATE_USER("创建用户"),
    EDIT_USER("编辑用户"),DELETE_USER("删除用户"),CREATE_DIRECTORY("创建目录"),
    EDIT_DIRECTORY("编辑目录"),DELETE_DIRECTORY("删除目录"),MANUAL_DELETE_LOG("手动删除日志"),
    SET_AUTO_DELETE_LOG("设置自动删除日志"),BATCH_IMPORT("批量导入"),BATCH_EMPORT("批量导出"),
    UNLOCK_USER_CONTINUOUSLY_INPUT_WRONG_PASSWORD("解锁密码连续输错的用户"),
    UNLOCK_USER_PASSWORD_AGING("解锁密码过期的用户"),CREATE_NODE_GROUP(" 创建节点组"),
    EDIT_NODE_GROUP("编辑节点组"),MOVE_NODE_GROUP("移动节点组"),DELETE_NODE_GROUP(" 删除节点组"),
    CREATE_CLIENT_TASK("创建客户端任务"),DELETE_CLIENT_TASK("删除客户端任务"),EDIT_CLIENT_TASK("编辑客户端任务"),
    START_CLIENT_TASK("生效客户端任务"),STOP_CLIENT_TASK("失效客户端任务"),MODIFY_WARN_CONFIG("修改告警配置"),
    ADD_SUBSCRIPTION("新增订阅"),MODIFY_SUBSCRIPTION("修改订阅"),DELETE_SUBSCRIPTION("删除订阅"),
    ADD_SUBSCRIBER("新增订阅用户"),MODIFY_SUBSCRIBER("修改订阅用户"),DELETE_SUBSCRIBER("删除订阅用户"),
    USER_CANCEL_SUBSCRIPTION("用户退订订阅"),ADD_CLUSTER_QUEUE("添加集群队列"),
    EDIT_CLUSTER_QUEUE("编辑集群队列"),DELETE_CLUSTER_QUEUE("删除集群队列"),
    CREATE_EXIT_FUNCTION("创建出口函数"),EDIT_EXIT_FUNCTION("编辑出口函数"),DELETE_EXIT_FUNCTION("删除出口函数"),
    MODIFY_NODE_ROUTE("修改节点路由"),MODIFY_CLIENT_AGENCY("修改客户端代理");
    private String context;
    private String getContext(){
       return this.context;
    }
    private EnumTest(String context){
       this.context = context;
    }
    public static void main(String[] args){
        int i=0;
       for(EnumTest name :EnumTest.values()){
           i++;
       System.out.println(name+" : "+name.getContext()+i);
       
       }
    }

} 
