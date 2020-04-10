package com.kolosov.synchronizer.service.lowLevel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FtpWorkerTest {

    @Autowired
    FtpWorker ftpWorker;
//
//    @Test
//    void getFileRelativePaths() {
//        List<Pair<String, Boolean>> fileRelativePaths = ftpWorker.getFileRelativePaths();
//        assertFalse(fileRelativePaths.isEmpty());
//    }
//
//    //предварительно поместить файл "test" в папку "test"
//    @Test
//    void delete() throws IOException {
//        ftpWorker.ftpClient.changeWorkingDirectory("/test");
//        FTPFile test = ftpWorker.ftpClient.listFiles("", file -> file.getName().equals("test"))[0];
//        assertNotNull(test);
//        ftpWorker.deleteFile(new AbstractSync("test", null, null));
//        FTPFile[] testFiles = ftpWorker.ftpClient.listFiles("", file -> file.getName().equals("test"));
//        assertEquals(0, testFiles.length);
//    }
//
//    @Test
//    void simple() {
//        List<AbstractSync> newFileRelativePaths = ftpWorker.getNewFileRelativePaths();
//        newFileRelativePaths.forEach(System.out::println);
//        Folder folder = new Folder("1", Location.PHONE);
//        Folder child = new Folder("2", Location.PHONE);
//        FileItem fileItem = new FileItem("3", Location.PHONE);
//        folder.list.add(child);
//        child.list.add(fileItem);
//        System.out.println(folder);
//
//    }

}