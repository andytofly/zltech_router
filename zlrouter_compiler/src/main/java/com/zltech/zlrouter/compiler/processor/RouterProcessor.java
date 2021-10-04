package com.zltech.zlrouter.compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.zltech.zlrouter.annotation.Route;
import com.zltech.zlrouter.annotation.RouteMeta;
import com.zltech.zlrouter.compiler.utils.Constant;
import com.zltech.zlrouter.compiler.utils.ProcessLog;
import com.zltech.zlrouter.compiler.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
/**
 处理器接收的参数 替代 {@link AbstractProcessor#getSupportedOptions()} 函数
 */
@SupportedOptions(Constant.ARGUMENTS_NAME)
/**
 * 指定使用的Java版本 替代 {@link AbstractProcessor#getSupportedSourceVersion()} 函数
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
/**
 * 注册给哪些注解的  替代 {@link AbstractProcessor#getSupportedAnnotationTypes()} 函数
 */
@SupportedAnnotationTypes(Constant.ANNOTATION_TYPE_ROUTE)

public class RouterProcessor extends AbstractProcessor {

    /**
     * key:组名 value:类名
     */
    private Map<String, String> rootMap = new TreeMap<>();
    /**
     * 分组 key:组名 value:对应组的路由信息
     */
    private Map<String, List<RouteMeta>> groupMap = new HashMap<>();

    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private Elements elementUtils;

    /**
     * type(类信息)工具类
     */
    private Types typeUtils;

    /**
     * 文件生成器 类/资源
     */
    private Filer filerUtils;

    private String moduleName;

    private ProcessLog log;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        //参数是模块名 为了防止多模块/组件化开发的时候 生成相同的 xx$$ROOT$$文件
        Map<String, String> options = processingEnvironment.getOptions();
        if (!Utils.isEmpty(options)) {
            moduleName = options.get(Constant.ARGUMENTS_NAME);
        }
        if (Utils.isEmpty(moduleName)) {
            log.i("Not set processor moudleName option,return...");
            return;
        }
        log.i("*******************************init RouterProcessor " + moduleName + " success !");

        log = ProcessLog.newLog(processingEnvironment.getMessager());
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filerUtils = processingEnvironment.getFiler();
    }


    /**
     *
     * @param set 使用了支持处理注解的节点集合
     * @param roundEnvironment 表示当前或是之前的运行环境,可以通过该对象查找找到的注解。
     * @return true 表示后续处理器不会再处理(已经处理)
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if(Utils.isEmpty(set) || Utils.isEmpty(moduleName))return false;
        //被Route注解的节点集合
        Set<? extends Element> rootElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        log.i(rootElements.toString()); //[com.zltech.zlrouter.app.TestActivity, com.zltech.zlrouter.app.MainActivity]
        if (!Utils.isEmpty(rootElements)) {
            processorRoute(rootElements);
        }
        return true;
    }

    private void processorRoute(Set<? extends Element> rootElements) {
        TypeElement activity = elementUtils.getTypeElement(Constant.ACTIVITY);
        TypeElement service = elementUtils.getTypeElement(Constant.COMPONENT);
        for (Element element : rootElements) {
            RouteMeta routeMeta;
            //类信息
            TypeMirror typeMirror = element.asType();
            log.i("Route class:" + typeMirror.toString());
            Route route = element.getAnnotation(Route.class);
            if (typeUtils.isSubtype(typeMirror, activity.asType())) {
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, route, element);
            } else if (typeUtils.isSubtype(typeMirror, service.asType())) {
                routeMeta = new RouteMeta(RouteMeta.Type.COMPONENT, route, element);
            } else {
                throw new RuntimeException("Just support Activity or Component Route: " + element);
            }
            categories(routeMeta);
        }
        TypeElement iRouteGroup = elementUtils.getTypeElement(Constant.IROUTE_GROUP);
        TypeElement iRouteRoot = elementUtils.getTypeElement(Constant.IROUTE_ROOT);


        if(iRouteGroup == null){
            log.i("igroup == null");
        }else{
            log.i(iRouteGroup.toString());
        }

        log.i(groupMap.toString());

        //生成Group记录分组表
        generatedGroup(iRouteGroup);

        //生成Root类 作用：记录<分组，对应的Group类>
        generatedRoot(iRouteRoot, iRouteGroup);
    }



    private void generatedGroup(TypeElement iRouteGroup) {
        log.i("begin generatedGroup....");
        //创建参数类型 Map<String, RouteMeta> atlas
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class));
        ParameterSpec altas = ParameterSpec.builder(parameterizedTypeName, "atlas").build();

        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_LOAD_INTO)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(altas);

            String groupName = entry.getKey();
            List<RouteMeta> groupData = entry.getValue();
            for (RouteMeta routeMeta : groupData) {
                //函数体的添加

//                log.i(ClassName.get(RouteMeta.class).simpleName());
//                log.i(ClassName.get(RouteMeta.Type.class).simpleName());
//                log.i(routeMeta.getType().name());
//                log.i(((TypeElement) routeMeta.getElement()).getSimpleName().toString());

                methodBuilder.addStatement("atlas.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        routeMeta.getPath(), //  /app/test
                        ClassName.get(RouteMeta.class), //  RouteMeta
                        ClassName.get(RouteMeta.Type.class), // RouteMeta
                        routeMeta.getType(), // Type.ACTIVITY
                        ClassName.get(((TypeElement) routeMeta.getElement())), // TestActivity
                        routeMeta.getPath(), // /app/test
                        routeMeta.getGroup()); // app
            }
            String groupClassName = Constant.NAME_OF_GROUP + groupName;
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                    .addSuperinterface(ClassName.get(iRouteGroup))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build();
            try {
                javaFile.writeTo(filerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }
            rootMap.put(groupName, groupClassName);

        }

        log.i("end generatedGroup....rootMap = "+rootMap.toString());
    }


    /**
     * Root类只有一个，按照module name来生成...
     * 一个Root类，也就是一个module可能有多个group，注入多个对应的Group类 对象
     * 生成Root类  作用：外层反射注入，调用该类方法，注入 <分组，对应的Group类> map的处理；
     * @param iRouteRoot
     * @param iRouteGroup
     */
    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRouteGroup) {
        //创建参数类型 Map<String,Class<? extends IRouteGroup>> routes>
        //Wildcard 通配符
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iRouteGroup))
                ));
        //参数 Map<String,Class<? extends IRouteGroup>> routes> routes
        ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "routes").build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_LOAD_INTO)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(parameter);
        //函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            methodBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(Constant.PACKAGE_OF_GENERATE_FILE, entry.getValue()));
        }
        //生成$Root$类
        String className = Constant.NAME_OF_ROOT + moduleName;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(iRouteRoot))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        try {
            JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build().writeTo(filerUtils);
            log.i("Generated RouteRoot：" + Constant.PACKAGE_OF_GENERATE_FILE + "." + className);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * 检查是否配置 group 如果没有配置 则从path截取出组名
     * @param routeMeta
     */
    private void categories(RouteMeta routeMeta) {
        if (routeVerify(routeMeta)) {
            log.i("Group : " + routeMeta.getGroup() + " path=" + routeMeta.getPath());
            //分组与组中的路由信息
            List<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (Utils.isEmpty(routeMetas)) {
                routeMetas = new ArrayList<>();
                routeMetas.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetas);
            } else {
                routeMetas.add(routeMeta);
            }
        } else {
            log.i("Group info error:" + routeMeta.getPath());
        }
    }


    /**
     * 验证path路由地址的合法性
     * @param routeMeta
     * @return
     */
    private boolean routeVerify(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();
        // 必须以 / 开头来指定路由地址
        if (!path.startsWith("/")) {
            return false;
        }
        //如果group没有设置 我们从path中获得group
        if (Utils.isEmpty(group)) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            //截取出的group还是空
            if (Utils.isEmpty(defaultGroup)) {
                return false;
            }
            routeMeta.setGroup(defaultGroup);
        }
        return true;
    }

}









