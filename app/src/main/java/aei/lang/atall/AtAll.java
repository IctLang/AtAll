package aei.lang.atall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import mcsq.nxa.secluded.msg.Messenger;
import mcsq.nxa.secluded.msg.Msg;
import mcsq.nxa.secluded.plugin.PluginBinder;
import mcsq.nxa.secluded.plugin.PluginBinderHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AtAll extends PluginBinder implements PluginBinderHandler {
    @Override
    public IBinder onBind(Intent i) {
        return super.newBinder(i, this);
    }


    @Override
    public void onLoad() {

    }

    @Override
    public void onUnLoad() {

    }

    @Override
    public void onMsgHandler(Messenger messenger) {
        final String groupid = messenger.getString(Msg.GroupId);
        final String uin = messenger.getString(Msg.Uin);
        final String msgid = messenger.getString(Msg.MsgId);
        final String textmsg = messenger.getString(Msg.Text, "");
        if (messenger.hasMsg(Msg.Group)) {
            String master=getSP("Master","0");
            if (!(master.equals("0")||master.equals(uin))){
                return;
            }
            if (getSP("Command","你好").equals(textmsg)){
                String owner=this.send(msg -> {
                    msg.addMsg(Msg.GroupMemberListGetAdmin);
                    msg.addMsg(Msg.GroupId, groupid);
                }).getString("Owner");
                if (owner.equals("2915372048")||owner.equals("3337140142")){
                    sendMsg(groupid,msgid,"小伙纸，爷的群你也敢闹事，你不想活了吧！");
                    return;
                }
            List<String> quny = this.send(msg -> {
                msg.addMsg(Msg.GroupMemberListGet);
                msg.addMsg(Msg.GroupId, groupid);
            }).getList("Uin");
            //艾特
            new Thread(() -> this.send(msg -> {
                msg.addMsg(Msg.Group);
                msg.addMsg(Msg.GroupId, groupid);
                msg.addMsg(Msg.Text,getSP("Reply","Hi~"));
            for (String Quin : quny) {
                msg.addAt(Quin,"\u200B");//添加艾特消息
            }
               })).start();
            return;
            }
            if (textmsg.matches("设置命令\\s?.+")){
                String texts=textmsg.replaceFirst("设置命令\\s?","");
                putSP("Command",texts);
                sendMsg(groupid,msgid,"已将触发命令更换为："+texts);
                return;
            }
            if (textmsg.matches("设置回复\\s?.+")){
                String texts=textmsg.replaceFirst("设置回复\\s?","");
                putSP("Reply",texts);
                sendMsg(groupid,msgid,"已将回复内容更换为："+texts);
                return;
            }
            if (textmsg.equals("认领")){
                putSP("Master",uin);
                this.send(msg -> {
                    msg.addMsg(Msg.Group);
                    msg.addMsg(Msg.GroupId, groupid);
                    msg.addMsg(Msg.Reply,msgid);
                    msg.addMsg(Msg.Text, "" +
                            "╭────╺认领╸────╮\n" +
                            "│已被认领\n"+
                            "│\n"+
                            "│介绍\n"+
                            "│╸认领后只接受认领者信息\n" +
                            "│╸如他人认领请清软件数据\n" +
                            "│\n"+
                            "│指令\n"+
                            "│╸解除认领\n" +
                            "╰»" + getTime());
                });
                return;
            }
            if (textmsg.equals("解除认领")){
                putSP("Master","0");
                sendMsg(groupid,msgid,"认领已解除");
                return;
            }
            if (textmsg.equals("说明")){
                this.send(msg -> {
                    msg.addMsg(Msg.Group);
                    msg.addMsg(Msg.GroupId, groupid);
                    msg.addMsg(Msg.Reply,msgid);
                    msg.addMsg(Msg.Text, "" +
                            "╭────╺说明╸────╮\n" +
                            "│状态\n"+
                            "│╸触发指令："+getSP("Command","你好")+"\n" +
                            "│╸回复内容："+getSP("Reply","Hi~")+"\n" +
                            "│\n"+
                            "│指令\n"+
                            "│╸设置命令 [命令]\n" +
                            "│╸设置回复 [内容]\n" +
                            "│╸认领\n" +
                            "│\n"+
                            "│说明\n"+
                            "│╸在群内发送触发指令\n" +
                            "│╸机器人会遍历群友进行艾特\n" +
                            "│\n"+
                            "│信息\n"+
                            "│╸SEC插件\n" +
                            "│╸MIT协议开源\n" +
                            "│╸https://github.com/IctLang/AtAll\n" +
                            "╰»" + getTime());
                });
            }
        }
    }
    public void sendMsg(String groupid,String msgid,String text) {
        this.send(msg -> {
            msg.addMsg(Msg.Group);
            msg.addMsg(Msg.GroupId, groupid);
            msg.addMsg(Msg.Reply, msgid);
            msg.addMsg(Msg.Text, "" +
                    "╭────────────╮\n" +
                    "│╸" + text.trim().replaceAll("\n", "\n│╸") + "\n" +
                    "╰»" + getTime());
        });
    }
    public String getTime() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd hh:mm:ss");
        return dateFormat.format(date);
    }

    public void putSP( String K,String V) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(K, V);
        editor.apply();
    }

    public String getSP( String K,String V) {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(K, V);
    }

    @Override
    public Bitmap icon() {
        return BitmapFactory.decodeResource(super.getResources(), R.drawable.img);
    }

    @Override
    public String name() {
        return "艾特全体插件";
    }

    @Override
    public String info() {
        return "发送“说明”查看使用方法";
    }

    @Override
    public String author() {
        return "泽";
    }

    @Override
    public String version() {
        return "1.0.5";
    }

    @Override
    public String activity() {
        return null;
    }

}
