package com.ljw.protocolcompiler;

import com.ljw.protocol.Protocol;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by Administrator on 2018/2/11 0011.
 */
@AutoService(Processor.class)
public class ProtocolProcessor extends AbstractProcessor {
    private Filer mFiler;
    private Messager mMessager;
    private Elements mElementsUtil;

    private static final String SUFFIX = "LJWProtocol";


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementsUtil = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();

    }

    /*
     * Element 的子类有以下4种
     * VariableElement //一般代表成员变量
     * ExecutableElement //一般代表类中的方法
     * TypeElement //一般代表代表类
     * PackageElement //一般代表Package
     * */

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(Protocol.class);
//        收集信息,分类
        Map<String, List<VariableElement>> cacheMap = new HashMap<>();
        for (Element element : elementSet) {
            VariableElement variableElement = (VariableElement) element;
            String className = getClassName(variableElement);
            System.out.print("遍历元素中");
            List<VariableElement> filedList = cacheMap.get(className);
            if (null == filedList) {
                filedList = new ArrayList<>();
                cacheMap.put(className, filedList);
            }
            filedList.add(variableElement);
        }
//        产生java文件
        Iterator<String> iterator = cacheMap.keySet().iterator();
        while (iterator.hasNext()) {
//            准备好生成java文件产生的信息
//            获取class的名字
            String className = iterator.next();
//            获取class中的所有成员属性
            List<VariableElement> cacheElements = cacheMap.get(className);
//            获取包名
            String packageName = getPackageName(cacheElements.get(0));
//            String packageName = "com.ljw.okserialport";
//            获取最后生成文件的文件名:className+"$"+SUFFIX
            String bindViewClass = "OkSerialPort_Protocol";
//            生成额外的文件,x写文件流
            Writer writer = null;
            try {
                JavaFileObject javaFileObject = mFiler.createSourceFile(bindViewClass);
//                拼接字符串
                writer = javaFileObject.openWriter();
//                获取简短新代理类名称
                String sampleClass = cacheElements.get(0).getEnclosingElement().getSimpleName().toString();
                String sampleBindViewClass = sampleClass + "$" + SUFFIX;
                writer.write(generateJavaCode(packageName, sampleBindViewClass, className, cacheElements));
                System.out.print("write the file");
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return false;
    }

    public String generateJavaCode(String mPackageName, String mProxyClassName, String className, List<VariableElement> cacheElements) {
        StringBuilder builder = new StringBuilder();
        builder.append("package " + mPackageName).append(";\n\n");
//        builder.append("import  com.ljw.bind_api.*;\n");
        builder.append("import java.util.*;\n");
        builder.append("import com.ljw.okserialport.serialport.bean.ProtocolBean;\n");
        builder.append("import com.ljw.okserialport.serialport.core.OkSerialPort_ProtocolManager;\n");
//        builder.append("public class ").append("LJWProtocolImp").append(" implements " + SUFFIX + "<" + className + ">");
        builder.append("public class ").append("OkSerialPort_Protocol");
        builder.append("\n{\n");
        builder.append("public static Map<Integer,ProtocolBean> mProtocolMap = new HashMap<>();\n");
        builder.append("public static List<byte[]> mHeartCommands = new ArrayList<>();\n");
        generateMethod(builder, className, cacheElements);
        generateMethod2(builder, className, cacheElements);
        builder.append("\n}\n");
        return builder.toString();
    }

    private void generateMethod(StringBuilder builder, String className, List<VariableElement> cacheElements) {
        for (VariableElement element : cacheElements) {
            Protocol bindView = element.getAnnotation(Protocol.class);
            int index = bindView.index();
            if (index == -1) {
                int frameHeader = bindView.frameHeader();
                if (frameHeader != -1) {//帧头
                    builder.append("public static int FRAMEHEADER = " + frameHeader + ";\n");
                }
                int frameHeader2 = bindView.frameHeader2();
                if (frameHeader2 != -1) {//帧头
                    builder.append("public static int FRAMEHEADER2 = " + frameHeader2 + ";\n");
                }
                int androidAdress = bindView.androidAdress();
                if (androidAdress != -1) {//安卓地址
                    builder.append("public static int ANDROIDADRESS = " + androidAdress + ";\n");
                }
                int hardwareAdress = bindView.hardwareAdress();
                if (hardwareAdress != -1) {//主控板地址
                    builder.append("public static int HARDWAREADRESS = " + hardwareAdress + ";\n");
                }
                int dealVersions = bindView.dealVersions();
                if (dealVersions != -1) {//协议版本
                    builder.append("public static int DEALVERSIONS = " + dealVersions + ";\n");
                }

                int dataLenIndex = bindView.dataLenIndex();
                if (dataLenIndex != Integer.MAX_VALUE) {//数据长度起始角标
                    builder.append("public static int DATALENINDEX = " + dataLenIndex + ";\n");
                }

                int dataStartIndex = bindView.dataStartIndex();
                if (dataStartIndex != Integer.MAX_VALUE) {//数据域起始角标
                    builder.append("public static int DATASTARTINDEX = " + dataStartIndex + ";\n");
                }
                int commandStartIndex = bindView.commandStartIndex();
                if (commandStartIndex != Integer.MAX_VALUE) {//命令码起始角标
                    builder.append("public static int COMMANDSTARTINDEX = " + commandStartIndex + ";\n");
                }
                int runningNumberIndex = bindView.runningNumberIndex();
                if (runningNumberIndex != Integer.MAX_VALUE) {////流水号起始角标
                    builder.append("public static int RUNNINGNUMBERINDEX = " + runningNumberIndex + ";\n");
                }
                int frameHeaderCount = bindView.frameHeaderCount();
                if (frameHeaderCount != -1) {//帧头字节数
                    builder.append("public static int FRAMEHEADERCOUNT = " + frameHeaderCount + ";\n");
                }
                int minDalaLen = bindView.minDalaLen();
                if (minDalaLen != -1) {//最小数据命令长度
                    builder.append("public static int MINDALALEN = " + minDalaLen + ";\n");
                }
                int checkCodeRule = bindView.checkCodeRule();
                if (checkCodeRule != -1) {//最小数据命令长度
                    builder.append("public static int CHECKCODERULE = " + checkCodeRule + ";\n");
                }
                int commandLen = bindView.commandLen();
                if (commandLen != -1) {
                    builder.append("public static int COMMANDLEN = " + commandLen + ";\n");
                }
                int protocolLen = bindView.protocolLen();
                if (protocolLen != -1) {
                    builder.append("public static int PROTOCOLLEN = " + protocolLen + ";\n");
                }

            }


        }

        builder.append("public static void bind(){");
        builder.append("\n");
        builder.append("OkSerialPort_ProtocolManager.getInstance().bind(DATALENINDEX,DATASTARTINDEX,COMMANDSTARTINDEX,RUNNINGNUMBERINDEX,FRAMEHEADERCOUNT,CHECKCODERULE,MINDALALEN,mProtocolMap,mHeartCommands);");
        builder.append("\n");
        builder.append("}\n");

    }

    private void generateMethod2(StringBuilder builder, String className, List<VariableElement> cacheElements) {
        builder.append("static {\n");
        for (VariableElement element : cacheElements) {
            Protocol bindView = element.getAnnotation(Protocol.class);
            int index = bindView.index();
            int length = bindView.length();
            byte value = bindView.value();
            if (index != -1) {
                builder.append("mProtocolMap.put(" + index + ",new ProtocolBean(" + index + "," + length + ",(byte)" + value + "));\n");

            }

        }
        for (VariableElement element : cacheElements) {
            Protocol bindView = element.getAnnotation(Protocol.class);
            byte heartbeatCommand = bindView.heartbeatCommand();
            if (heartbeatCommand != -1) {
                builder.append("mHeartCommands.add(new byte[]{(byte)"+heartbeatCommand+"});\n");
            }

        }

        builder.append("\n }\n");


    }

    private String getPackageName(VariableElement variableElement) {
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
//        String packeName = mElementsUtil.getPackageOf(typeElement).getQualifiedName().toString();
        String packeName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        return packeName;
    }

    private String getClassName(VariableElement element) {
        String packageName = getPackageName(element);
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String className = typeElement.getSimpleName().toString();
        return packageName + "." + className;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Protocol.class.getCanonicalName());
        return annotations;
//        return super.getSupportedAnnotationTypes();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
//        return super.getSupportedSourceVersion();
    }


}
