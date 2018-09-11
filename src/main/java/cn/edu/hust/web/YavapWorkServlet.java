package cn.edu.hust.web;

import cn.edu.hust.dto.RangeInfo;
import cn.edu.hust.engine.api.ClassFile;
import cn.edu.hust.service.upload.FileUtils;
import cn.edu.hust.service.wrap.ClassFileBlockUtil;
import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.parser.JSONParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.font.NumericShaper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

@WebServlet("/work")
public class YavapWorkServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/text");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        String offset = req.getParameter("offset");
        //String path = this.getClass().getResource("/").getPath() + "C.class";
        //String path = this.getClass().getResource("/").getPath() + "ClassFile.class";
        String path = URLDecoder.decode((String) req.getParameter("path"), "utf-8");
        byte[] data = FileUtils.getFileByteData(path);
        int[] pool = ClassFile.getConstantPool(data);
        List<RangeInfo> rangeInfos = ClassFileBlockUtil.getClassFileBlockByStartOffset(data, pool, Integer.parseInt(offset));
        out.print(JSON.toJSON(rangeInfos));
        System.out.println(JSON.toJSON(rangeInfos));
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
