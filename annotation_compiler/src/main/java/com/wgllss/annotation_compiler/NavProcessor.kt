package com.wgllss.annotation_compiler

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.wgllss.annotations.ActivityDestination
import com.wgllss.annotations.Destination
import com.wgllss.annotations.FragmentDestination
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
class NavProcessor : AbstractProcessor() {
    private val OUTPUT_FILE_NAME = "destination.json"
    private var filer: Filer? = null
    private var messager: Messager? = null

    private var mElementUtils: Elements? = null

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        filer = processingEnv?.filer
        mElementUtils = processingEnv?.elementUtils
        //日志打印,在java环境下不能使用android.util.log.e()
        messager = processingEnv!!.messager
    }

    //指定处理的版本
    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    //给到需要处理的注解
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val types: LinkedHashSet<String> = LinkedHashSet()
        getSupportedAnnotations().forEach { clazz: Class<out Annotation> ->
            types.add(clazz.canonicalName)
        }
        return types
    }

    private fun getSupportedAnnotations(): Set<Class<out Annotation>> {
        val annotations: LinkedHashSet<Class<out Annotation>> = LinkedHashSet()
        // 需要解析的自定义注解
        annotations.add(FragmentDestination::class.java)
        annotations.add(ActivityDestination::class.java)
        return annotations
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        //通过处理器环境上下文roundEnv分别获取 项目中标记的FragmentDestination.class 和ActivityDestination.class注解。
        //此目的就是为了收集项目中哪些类 被注解标记了

        //通过处理器环境上下文roundEnv分别获取 项目中标记的FragmentDestination.class 和ActivityDestination.class注解。
        //此目的就是为了收集项目中哪些类 被注解标记了
        val fragmentElements = roundEnv!!.getElementsAnnotatedWith(FragmentDestination::class.java)
        val activityElements = roundEnv!!.getElementsAnnotatedWith(ActivityDestination::class.java)

        if (!fragmentElements.isEmpty() || !activityElements.isEmpty()) {
            val destMap: LinkedHashMap<String, Destination> = LinkedHashMap<String, Destination>()
            val packageName = "com.wgllss.ssmusic"
            var greeterClass = "NavigationConfig";
            val klass = LinkedHashMap::class
                .parameterizedBy(String::class, Destination::class)
                .copy(nullable = true)
            val property = PropertySpec.builder("sDestConfig", klass, KModifier.PRIVATE)
                .mutable()
                .initializer("null")
                .build()

            val create = MemberName("kotlin.collections", "LinkedHashMap")
            val sb = StringBuilder()
            sb.append("if (sDestConfig == null) {\n")
            sb.append("%N = %N<String,Destination>()\n")
            handleDestination(fragmentElements, FragmentDestination::class.java, sb, destMap)
            handleDestination(activityElements, ActivityDestination::class.java, sb, destMap)
            sb.append("}\n")
            sb.append("return sDestConfig!!")
            val funspec = FunSpec.builder("getDestConfig")
                .returns(LinkedHashMap::class.parameterizedBy(String::class, Destination::class))
                .addStatement(sb.toString(), "sDestConfig", create)
                .build()
            val typeSpecClassBuilder = TypeSpec.objectBuilder(greeterClass)//类名
                .addProperty(property)
                .addFunction(funspec)
            val file = FileSpec.builder(packageName, greeterClass)
                .addType(typeSpecClassBuilder.build()).build()
            filer?.let { file.writeTo(it) }
        }
        return true
    }

    private fun handleDestination(
        elements: Set<Element>, annotationClaz: Class<out Annotation>,
        sb: StringBuilder, destMap: HashMap<String, Destination>
    ) {
        elements.forEach {
            //TypeElement是Element的一种。
            //如果我们的注解标记在了类名上。所以可以直接强转一下。使用它得到全类名
            val typeElement = it as TypeElement
            //全类名com.mooc.ppjoke.home
            val clazName = typeElement.qualifiedName.toString()
            //页面的id.此处不能重复,使用页面的类名做hascode即可,navigation框架内部走的Integer
            val id = Math.abs(clazName.hashCode())
            //页面的pageUrl相当于隐士跳转意图中的host://schem/path格式
            var pageUrl: String? = null
            //是否需要登录
            var needLogin = false
            //是否作为首页的第一个展示的页面
            var asStarter = false
            //标记该页面是fragment 还是activity类型的
            var isFragment = false
            var label: String? = null
            var iconId: Int? = 0
            val annotation = it.getAnnotation(annotationClaz)
            if (annotation is FragmentDestination) {
                val dest = annotation
                pageUrl = dest.pageUrl
                asStarter = dest.asStarter
                needLogin = dest.needLogin
                iconId = dest.iconId
                isFragment = true
                label = dest.label
            } else if (annotation is ActivityDestination) {
                val dest = annotation
                pageUrl = dest.pageUrl
                asStarter = dest.asStarter
                needLogin = dest.needLogin
                iconId = dest.iconId
                isFragment = false
            }
            if (destMap.containsKey(pageUrl)) {
                messager!!.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl：$clazName")
            } else {
                val destination = Destination(isFragment, asStarter, needLogin, "\"${pageUrl}\"", "\"${clazName}\"", id, "\"${label}\"", iconId!!)
                sb.append("sDestConfig?.put(\"${pageUrl}\",${destination})\n")
                if (pageUrl != null) {
                    destMap[pageUrl] = destination
                }
            }
        }
    }
}