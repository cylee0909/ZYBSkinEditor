import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: chenjishi
 * Date: 13-10-12
 * Time: AM11:45
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {

    private static final int BUFFER_SIZE = 8 * 1024; // 8 KB
    
	/**
	 * 通过文件路劲获取保存到本地的的Model
	 * @param path
	 * @return
	 */
    public static Object unserializeObject(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        ObjectInputStream ois = null;
        Object o = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(path));
            o = ois.readObject();
        } catch (Exception e) {
            file.delete();
            return null;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }
        }
        return o;
    }

    /**
     * 将文件序列化并且保存到本地
     * @param path
     * @param o
     * @return
     */
    public static boolean serializeObject(String path, Object o) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(o);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }


    public static boolean writeFile(String path,byte[] data){
        if(data == null){
            return false;
        }else{
            BufferedOutputStream bos = null;
            try{
                bos = new BufferedOutputStream(new FileOutputStream(path));
                bos.write(data,0,data.length);
                return true;
            }catch(Exception e){
                e.printStackTrace();

                return false;
            }finally {
                if(bos != null){
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 保存下载的文件到本地
     * @param savePath 保存路径
     * @param inFile 要保存的文件
     * @return 是否保存成功
     */
    public static boolean writeFile(String savePath, File inFile) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(inFile));
            bos = new BufferedOutputStream(new FileOutputStream(savePath));
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] readFile(File file) {
        byte[] result = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            int fileSize = (int) file.length();
            result = new byte[fileSize];
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            bis.read(result, 0, fileSize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bis!= null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    public static boolean delFile(File file){
        if(null == file){
            return false;
        }
        if(file.exists()){
            return file.delete();
        }
        return true;
    }
    public static boolean delFile(String path) {
        return delFile(new File(path));
    }

    /**
     * 清空目录的内容（不包括目录本身)
     * @param dir
     */
    public static void clearDir(File dir)
    {
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            if(files != null){
                for(File file : files){
                    if(file.isDirectory()){
                        deleteDir(dir);
                    }else{
                        delFile(file);
                    }
                }
            }
        }
    }
    /**
     * 清空整个目录（包括目录本身)
     * @param dir
     */
    public static void deleteDir(File dir)
    {
        if(dir.isDirectory())
        {
            File[] files=dir.listFiles();
            if(files == null){
                dir.delete();
                return;
            }
            for(int i=0;i<files.length;i++)
            {
                deleteDir(files[i]);
            }
            dir.delete();
        }
        else
        {
            dir.delete();
        }
    }
    public static byte[] readInputStream(InputStream input) {
        byte[] data = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
    /**
     * 复制IO流
     *
     * @param is
     * @param os
     * @throws IOException
     */
    public static void copyStream(InputStream is, OutputStream os) throws IOException {

        if (is == null || os == null) {
            throw new IOException("Argument is null.");
        }

        byte[] bytes = new byte[BUFFER_SIZE];
        while (true) {
            int count = is.read(bytes, 0, BUFFER_SIZE);
            if (count == -1) {
                break;
            }
            os.write(bytes, 0, count);
        }
    }
    public static byte[] gunzip(byte[] input){
        GZIPInputStream inputStream = null;
        ByteArrayOutputStream bos = null;
        try {
            inputStream = new GZIPInputStream(new ByteArrayInputStream(input));
            bos = new ByteArrayOutputStream();
            copyStream(inputStream,bos);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(bos != null) {
            return bos.toByteArray();
        }else{
            return null;
        }
    }
    /**
     * 复制文件
     * @param src 源文件
     * @param des 目的文件
     * @throws IOException
     */
    public static void copy(File src, File des) throws IOException{
        if(des.exists()){
            des.delete();
        }
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(des);
        try{
            FileUtils.copyStream(fis, fos);
        }catch (IOException e){
            throw e;
        }finally {
            fis.close();
            fos.close();
        }
    }

    /**
     * 剪切文件
     * @param src 源文件
     * @param des 目的文件
     * @throws IOException
     */
    public static void cut(File src, File des) throws  IOException {
        copy(src, des);
        delFile(src);
    }

    public static boolean unzip(String filePath, String outPath, boolean delSrc, int bufSize) {
        ZipInputStream inputStream = null;
        BufferedOutputStream out = null;
        InputStream in = null;
        try {
            inputStream = new ZipInputStream(new FileInputStream(filePath));
            File outDirectory = new File(outPath);
            if(!outDirectory.isDirectory()) {
                if(!outDirectory.mkdirs()) {
                    throw new FileNotFoundException();
                }
            }
            in = new BufferedInputStream(inputStream);

            bufSize = Math.max(bufSize, 1024);
            ZipEntry entry;
            byte[] buffer = new byte[bufSize];
            while((entry = inputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if(!entry.isDirectory()) {
                    File file = new File(outPath , name);
                    File folder = file.getParentFile();
                    if(folder == null || (!folder.exists() && !folder.mkdirs())) {
                        throw new FileNotFoundException();
                    }
                    out = new BufferedOutputStream(new FileOutputStream(file));
                    int len;
                    while((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                }
            }
            return true;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }finally{
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (delSrc) {
                FileUtils.delFile(filePath);
            }
        }
        return false;
    }

    public static boolean isApk(File file){
        if(file == null || !file.exists()){
            return false;
        }
        ZipInputStream zipInputStream = null;
        try{
            zipInputStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry ze;
            while((ze = zipInputStream.getNextEntry()) != null){
                if(ze.getName().equals("AndroidManifest.xml")){
                    return true;
                }
            }
        }catch (Exception e){

        }
        finally {
            try {
                if(zipInputStream != null){
                    zipInputStream.close();
                }
            } catch (IOException e) {
            }
        }
        return false;
    }
    public static String md5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
    /**
     * 批量压缩文件（夹）
     *
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile 生成的压缩文件
     * @param comment 压缩文件的注释
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFiles(Collection<File> resFileList, File zipFile, String comment)
            throws IOException {
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        for (File resFile : resFileList) {
            zipFile(resFile, zipout, "");
        }
        zipout.setComment(comment);
        zipout.close();
    }
    /**
     * 批量压缩文件（夹）
     *
     * @param src 要压缩的文件（夹）
     * @param zipFile 生成的压缩文件
     * @param comment 压缩文件的注释
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFile(File src, File zipFile, String comment)
            throws IOException {
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        zipFile(src, zipout, "");
        zipout.setComment(comment);
        zipout.close();
    }
    /**
     * 压缩文件
     *
     * @param resFile 需要压缩的文件（夹）
     * @param zipout 压缩的目的文件
     * @param rootpath 压缩的文件路径
     * @throws FileNotFoundException 找不到文件时抛出
     * @throws IOException 当压缩过程出错时抛出
     */
    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
            throws FileNotFoundException, IOException {
        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)
                + resFile.getName();
        rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipout, rootpath);
            }
        } else {
            byte buffer[] = new byte[BUFF_SIZE];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
                    BUFF_SIZE);
            zipout.putNextEntry(new ZipEntry(rootpath));
            int realLength;
            while ((realLength = in.read(buffer)) != -1) {
                zipout.write(buffer, 0, realLength);
            }
            in.close();
            zipout.flush();
            zipout.closeEntry();
        }
    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    /**
     *
     * @param srcFileName
     * @param destFileName
     * @param srcCoding
     * @param destCoding
     * @throws IOException
     */
    public static void copyFile(File srcFileName, File destFileName, String srcCoding, String destCoding) throws IOException {// 把文件转换为GBK文件
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFileName), srcCoding));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFileName), destCoding));
            char[] cbuf = new char[1024 * 5];
            int len = cbuf.length;
            int off = 0;
            int ret = 0;
            while ((ret = br.read(cbuf, off, len)) > 0) {
                off += ret;
                len -= ret;
            }
            bw.write(cbuf, 0, off);
            bw.flush();
        } finally {
            if (br != null)
                br.close();
            if (bw != null)
                bw.close();
        }
    }
}
