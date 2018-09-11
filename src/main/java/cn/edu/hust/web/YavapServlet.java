package cn.edu.hust.web;

import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.api.ClassFile;
import cn.edu.hust.service.upload.FileUtils;
import cn.edu.hust.service.wrap.ClassFileBlockUtil;
import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/show")
public class YavapServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/text");
        resp.setCharacterEncoding("UTF-8");
        String errorMsg = (String) req.getParameter("message");
        String path = "";
        if ("suc".equals(errorMsg)) {
            path = (String) req.getParameter("path");
        } else {
            path = this.getClass().getResource("/").getPath() + "C.class";
        }
        if (errorMsg == null)
            errorMsg = "suc";
        req.setAttribute("error", errorMsg);
        List<String> content = FileUtils.createPageContent(path);
        byte[] data = FileUtils.getFileByteData(path);
        int[] pool = ClassFile.getConstantPool(data);
        List<RangeInfo> blocksStart = ClassFileBlockUtil.getClassFileBlockStartOffset(data, pool);
        req.setAttribute("content", JSON.toJSON(content));
        req.setAttribute("blocksStart", JSON.toJSON(blocksStart));
        req.setAttribute("path", path);
        getServletContext().getRequestDispatcher("/class-block.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
