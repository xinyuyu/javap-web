package cn.edu.hust.web;

import cn.edu.hust.engine.api.ClassFile;
import cn.edu.hust.engine.api.ClassParser;
import cn.edu.hust.service.upload.FileUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet("/upload")
public class YavapFileUploadServlet extends HttpServlet {
    private static final int MAX_FILE_SIZE = 1024 * 1024;
    private static final String DIR = "/tmp/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setHeaderEncoding("UTF-8");
        String uploadPath = DIR + File.separator + req.getSession().getId();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        String filePath = "";
        String msg = "suc";
        try {
            List<FileItem> formItems = upload.parseRequest(req);
            if (formItems != null && formItems.size() > 0) {
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        item.write(storeFile);
                    }
                }
            }
            ClassParser classParser = new ClassParser(new FileInputStream(new File(filePath)));
            req.setAttribute("message", msg);
            if (!classParser.isClassFile()) {
                msg = "not class file";
                req.setAttribute("message", msg);
            }
        } catch (Exception ex) {
            msg = "exception";
            req.setAttribute("message", msg);
        }

        // 跳转到 message.jsp
        req.setAttribute("path", filePath);
        String enCodePath = URLEncoder.encode(filePath, "utf-8");
        resp.sendRedirect(req.getContextPath() + "/show?path=" + enCodePath + "&message=" + msg);
    }
}
