import java.io.File
import java.util.*

/**
 * Created by cylee on 2017/9/6.
 */

val drawables = mapOf<String, String>(
        "拍照搜题_n" to "skin_ask_fragment_ask_bg_normal",
        "拍照搜题_p" to "skin_ask_fragment_ask_bg_pressed",
        "拍照_n" to "skin_index_shot_new_large_normal_1",
        "拍照_p" to "skin_index_shot_new_large_pressed_2_4",
        "更多" to "skin_index_shot_question_new_large_more",
        "1对1_n" to "skin_tint_index_index_fudao_new_normal_1",
        "1对1_p" to "skin_tint_index_index_fudao_new_pressed_1",
        "1对1_1" to "skin_tint_index_index_fudao_new_pressed_1",
        "1对1_2" to "skin_tint_index_index_fudao_new_pressed_2",
        "1对1_3" to "skin_tint_index_index_fudao_new_pressed_3",
        "1对1_4" to "skin_tint_index_index_fudao_new_pressed_4",
        "一课_n" to "skin_tint_index_index_live_new_normal_1",
        "一课_p" to "skin_tint_index_index_live_new_pressed_1",
        "一课_1" to "skin_tint_index_index_live_new_pressed_1",
        "一课_2" to "skin_tint_index_index_live_new_pressed_2",
        "一课_3" to "skin_tint_index_index_live_new_pressed_3",
        "一课_4" to "skin_tint_index_index_live_new_pressed_4",
        "我的_n" to "skin_tint_index_index_me_new_normal_1",
        "我的_p" to "skin_tint_index_index_me_new_pressed_1",
        "我的_1" to "skin_tint_index_index_me_new_pressed_1",
        "我的_2" to "skin_tint_index_index_me_new_pressed_2",
        "我的_3" to "skin_tint_index_index_me_new_pressed_3",
        "我的_4" to "skin_tint_index_index_me_new_pressed_4",
        "一练_n" to "skin_tint_index_index_more_new_normal_1",
        "一练_p" to "skin_tint_index_index_more_new_pressed_1",
        "一练_1" to "skin_tint_index_index_more_new_pressed_1",
        "一练_2" to "skin_tint_index_index_more_new_pressed_2",
        "一练_3" to "skin_tint_index_index_more_new_pressed_3",
        "一练_4" to "skin_tint_index_index_more_new_pressed_4",
        "背景图xhdpi" to "skin_ask_fragment_bg",
        "大按钮_n" to "skin_user_card_top_bg.9",
        "未登录_n" to "skin_user_login_bg_normal.9",
        "未登录_p"  to "skin_user_login_bg_pressed.9",
        "底部bar" to "skin_index_tab_drawable"
)

fun main(args: Array<String>) {
    var path = args[0]
    var sourceFolder = File(path)
    if (!sourceFolder.exists()) {
        error(sourceFolder.absolutePath + " not exist")
    }

    var dstFolder = File(sourceFolder, "dst");
    if (dstFolder.exists()) {
        FileUtils.delFile(dstFolder);
    }
    dstFolder.mkdirs();

    var templateFolder = File(sourceFolder, "template");
    println("拷贝模板文件...")
    // 拷贝模板文件
    FileUtils.copyDirectiory(templateFolder.absolutePath, dstFolder.absolutePath)
    println("拷贝模板文件完成...")

    var androidFolder = File(sourceFolder, "安卓")
    if (!androidFolder.exists()) {
        error(androidFolder.absolutePath + " not exist")
    }

    // 拷贝color
    var colorValue = File(androidFolder, "colors.xml")
    if (colorValue.exists()) {
        colorValue.copyTo(File(dstFolder, "res/values/colors.xml"), true)
        println("拷贝colors文件")
    }

    println("拷贝图片...")
    var hdpiFolder = File(androidFolder, "drawable-hdpi")
    var xhdpiFolder = File(androidFolder, "drawable-xhdpi")
    var dstHdpi = File(dstFolder, "res/drawable-hdpi-v4");
    var dstXhdpi = File(dstFolder, "res/drawable-xhdpi-v4")
    copyDrawableFile(hdpiFolder, dstHdpi)
    copyDrawableFile(xhdpiFolder, dstXhdpi)

    processAnimIcon(dstFolder, "index_index_fudao")
    processAnimIcon(dstFolder, "index_index_live")
    processAnimIcon(dstFolder, "index_index_me")
    processAnimIcon(dstFolder, "index_index_more")

    println("资源替换完成，准备打包中...")
    var current = System.currentTimeMillis()
    Runtime.getRuntime().exec("java -jar ./apktool_2.0.3.jar b dst");
    println("打包完成！pkg time = "+(System.currentTimeMillis() - current))
//    File(dstFolder, "dist").listFiles().forEach {
//        it.copyTo(File(dstFolder.parentFile, it.name))
//    }
//    FileUtils.delFile(dstFolder)
}

/**
 * 处理icon动画生成
 * eg index_index_fudao
 */
fun processAnimIcon(dst: File, key: String) {
    var dstDrawableFile = File(dst, "res/drawable/skin_tint_" + key + "_new.xml")
    FileUtils.delFile(dstDrawableFile)
    dstDrawableFile.createNewFile()
    var tmpContent = """<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
<item android:state_selected="true">
        <animation-list android:oneshot="true">
            """
    var xhdpiFolder = File(dst, "res/drawable-xhdpi-v4")
    var firstAnimName = "skin_tint_${key}_new_pressed_1"
    var animFiles = xhdpiFolder.listFiles { file, s -> s.contains(key+"_new_pressed") }
    if (animFiles.size == 4) {
        animFiles.sortWith(Comparator { t1, t2 ->
            t1.name.compareTo(t2.name)
        })
        animFiles.forEach {
            tmpContent += """<item android:duration="150" android:drawable="@drawable/${it.nameWithoutExtension}"/>"""+"\n\t\t"
        }
    } else if (animFiles.size == 1){
        tmpContent += """<item android:state_selected="true" android:duration="150" android:drawable="@drawable/${firstAnimName}"/>"""+"\n\t\t"
    } else {
        error("动画仅支持两帧和四帧.... 请检查${key}相关资源")
    }
    tmpContent += """
     </animation-list>
    </item>
    <item android:state_pressed="true" android:drawable="@drawable/${firstAnimName}" />
    <item android:drawable="@drawable/skin_tint_${key}_new_normal_1" />
</selector>
    """
    dstDrawableFile.writeText(tmpContent)
}

fun copyDrawableFile(source: File, dst: File) {
    if (dst.exists()) {
        dst.delete()
    }
    dst.mkdirs()
    source.listFiles{it->it.name.endsWith("png")}.forEach {
        it ->
        var name = it.nameWithoutExtension;
        if (drawables.containsKey(name)) {
            var dstName = drawables.get(name)
            it.copyTo(File(dst, dstName + ".png"), true)
        } else {
            println("=================== ERROR =========== 无法识别的文件名 " + it.absoluteFile)
        }
    }
}

fun error(msg: String) {
    println(msg)
    System.exit(0);
}