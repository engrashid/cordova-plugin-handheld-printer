package cordova.plugin.print;

import org.apache.cordova.*;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rt.printerlibrary.bean.SerialPortConfigBean;
import com.rt.printerlibrary.cmd.Cmd;
import com.rt.printerlibrary.cmd.EscFactory;
import com.rt.printerlibrary.connect.PrinterInterface;
import com.rt.printerlibrary.enumerate.CommonEnum;
import com.rt.printerlibrary.enumerate.SettingEnum;
import com.rt.printerlibrary.exception.SdkException;
import com.rt.printerlibrary.factory.cmd.CmdFactory;
import com.rt.printerlibrary.factory.connect.PIFactory;
import com.rt.printerlibrary.factory.connect.SerailPortFactory;
import com.rt.printerlibrary.factory.printer.PrinterFactory;
import com.rt.printerlibrary.factory.printer.ThermalPrinterFactory;
import com.rt.printerlibrary.observer.PrinterObserver;
import com.rt.printerlibrary.observer.PrinterObserverManager;
import com.rt.printerlibrary.printer.RTPrinter;
import com.rt.printerlibrary.setting.TextSetting;
import com.rt.printerlibrary.utils.PrinterPowerUtil;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.rt.printerlibrary.enumerate.CommonEnum.ALIGN_MIDDLE;

/**
 * This class echoes a string called from JavaScript.
 */
public class PrintWrapper extends CordovaPlugin implements PrinterObserver{

    private RTPrinter rtPrinter = null;
    private PrinterFactory printerFactory;
    private Object configObj;
    private PrinterPowerUtil printerPowerUtil;//To switch AP02 printer power.
    private String test = "intializw";
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Context context=this.cordova.getActivity().getApplicationContext();
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        PrinterObserverManager.getInstance().add(this);
        configObj = new SerialPortConfigBean().getDefaultConfig();
        this.connectSerialPort((SerialPortConfigBean) configObj);
        printerPowerUtil = new PrinterPowerUtil(context);
        //BaseApplication.instance.setCurrentCmdType(MainActivity.CMD_ESC);
        // printerFactory = new ThermalPrinterFactory();
        //  rtPrinter = printerFactory.create();
        //  PrinterObserverManager.getInstance().add(this);
        //  configObj = new SerialPortConfigBean().getDefaultConfig();
        // printerPowerUtil = new PrinterPowerUtil(this);
     } 
 

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }else if(action.equals("myMethod")){
            String message = args.getString(0);
            this.myMethod(message, callbackContext);
            return true;
        }else if(action.equals("printMethod")){
            String message = args.getString(0);
                this.printReceipt(message, callbackContext);
                return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            
             Context context=this.cordova.getActivity().getApplicationContext(); 
             printerFactory = new ThermalPrinterFactory();
            rtPrinter = printerFactory.create();
            PrinterObserverManager.getInstance().add(this);
            configObj = new SerialPortConfigBean().getDefaultConfig();
            this.connectSerialPort((SerialPortConfigBean) configObj);
            printerPowerUtil = new PrinterPowerUtil(context);
            //callbackContext.success("printer code working!!");
            // this.escSelftestPrint();
            callbackContext.success(test);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void myMethod(String message, CallbackContext callbackContext) {
        //showToast("I am a message displayed by android toast");
        //webView.loadUrl("javascript:console.log('hello');");
        if (message != null && message.length() > 0) {
            callbackContext.success("Java Method Callback");
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

     /**
     * Show a toast with some message
     */
    public void showToast(String toLog){
        Context context = cordova.getActivity();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, toLog, duration);
        toast.show();
    }
    
    @Override
    public void printerObserverCallback(final PrinterInterface printerInterface, final int state) {
        test = "print ovserver inserted";
        switch (state) {
            case CommonEnum.CONNECT_STATE_SUCCESS:
                test = "print connected";
                rtPrinter.setPrinterInterface(printerInterface);
                //BaseApplication.getInstance().setRtPrinter(rtPrinter);
                break;
            case CommonEnum.CONNECT_STATE_INTERRUPTED:
                test = "print interrepted";
               // BaseApplication.getInstance().setRtPrinter(null);
                break;
            default:
                break;
        }
    }

    private void printReceipt(String message, CallbackContext callbackContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CmdFactory escFac = new EscFactory();
                    Cmd escCmd = escFac.create();
                    escCmd.setChartsetName("UTF-8");
                    TextSetting textSetting = new TextSetting();
                    textSetting.setAlign(ALIGN_MIDDLE);//对齐方式-左对齐，居中，右对齐
                    textSetting.setBold(SettingEnum.Enable);//加粗
                    textSetting.setUnderline(SettingEnum.Disable);//下划线
                    textSetting.setIsAntiWhite(SettingEnum.Disable);//反白
                    textSetting.setDoubleHeight(SettingEnum.Enable);//倍高
                    textSetting.setDoubleWidth(SettingEnum.Enable);//倍宽
                    textSetting.setItalic(SettingEnum.Disable);//斜体
                    textSetting.setIsEscSmallCharactor(SettingEnum.Disable);//小字体
                    escCmd.append(escCmd.getHeaderCmd());//初始化
                    escCmd.append(escCmd.getTextCmd(textSetting, "Wow"));
                    escCmd.append(escCmd.getLFCRCmd());//回车换行

                    textSetting.setIsEscSmallCharactor(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleHeight(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    escCmd.append(escCmd.getTextCmd(textSetting, "+0000000000"));

                    escCmd.append(escCmd.getLFCRCmd());
                    textSetting.setUnderline(SettingEnum.Enable);
                    escCmd.append(escCmd.getTextCmd(textSetting, "3123 1233 1231 2131"));
                    escCmd.append(escCmd.getTextCmd(textSetting, "wearegood@payonmobi.com"));

                    escCmd.append(escCmd.getLFCRCmd());
                    escCmd.append(escCmd.getLFCRCmd());

                    escCmd.append(escCmd.getLFCRCmd());
                    escCmd.append(escCmd.getLFCRCmd());
                    escCmd.append(escCmd.getLFCRCmd());

                    rtPrinter.writeMsg(escCmd.getAppendCmds());
                    callbackContext.success("Printing  complete");
                } catch (Exception e) {
                    e.printStackTrace();
                    callbackContext.error(e.getMessage().toString());
                } 
            }
        }).start();
    }

    // public void printTemplate() {
    //                 CmdFactory escFac = new EscFactory();
    //                 Cmd escCmd = escFac.create();
    //                 escCmd.setChartsetName("UTF-8");
    //                 TextSetting textSetting = new TextSetting();
    //                 textSetting.setAlign(ALIGN_MIDDLE);//对齐方式-左对齐，居中，右对齐
    //                 textSetting.setBold(SettingEnum.Enable);//加粗
    //                 textSetting.setUnderline(SettingEnum.Disable);//下划线
    //                 textSetting.setIsAntiWhite(SettingEnum.Disable);//反白
    //                 textSetting.setDoubleHeight(SettingEnum.Enable);//倍高
    //                 textSetting.setDoubleWidth(SettingEnum.Enable);//倍宽
    //                 textSetting.setItalic(SettingEnum.Disable);//斜体
    //                 textSetting.setIsEscSmallCharactor(SettingEnum.Disable);//小字体
    //                 escCmd.append(escCmd.getHeaderCmd());//初始化
    //                 escCmd.append(escCmd.getTextCmd(textSetting, "Wow"));
    //                 escCmd.append(escCmd.getLFCRCmd());//回车换行

    //                 textSetting.setIsEscSmallCharactor(SettingEnum.Enable);
    //                 textSetting.setBold(SettingEnum.Disable);
    //                 textSetting.setDoubleHeight(SettingEnum.Disable);
    //                 textSetting.setDoubleWidth(SettingEnum.Disable);
    //                 escCmd.append(escCmd.getTextCmd(textSetting, "+0000000000"));

    //                 escCmd.append(escCmd.getLFCRCmd());
    //                 textSetting.setUnderline(SettingEnum.Enable);
    //                 escCmd.append(escCmd.getTextCmd(textSetting, "3123 1233 1231 2131"));
    //                 escCmd.append(escCmd.getTextCmd(textSetting, "wearegood@payonmobi.com"));

    //                 escCmd.append(escCmd.getLFCRCmd());
    //                 escCmd.append(escCmd.getLFCRCmd());

    //                 escCmd.append(escCmd.getLFCRCmd());
    //                 escCmd.append(escCmd.getLFCRCmd());
    //                 escCmd.append(escCmd.getLFCRCmd());

    //                 rtPrinter.writeMsg(escCmd.getAppendCmds());
    // }

    private void connectSerialPort(SerialPortConfigBean serialPortConfigBean) {
        PIFactory piFactory = new SerailPortFactory();
        PrinterInterface printerInterface = piFactory.create();
        printerInterface.setConfigObject(serialPortConfigBean);
        rtPrinter.setPrinterInterface(printerInterface);
        try {
            rtPrinter.connect(serialPortConfigBean);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
