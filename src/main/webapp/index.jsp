<%--
  Created by IntelliJ IDEA.
  User: yuyu
  Date: 2018/9/2
  Time: 下午9:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
</head>
<html>
<body>
<div id="code">
    <c:forEach items="${content}" var="item">${item}</c:forEach>
    ${content}
</div>
<div id="note">

</div>
</body>
</html>
<script type="application/javascript">
    function show(label) {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
                var responseText = xhr.responseText;
                var obj = JSON.parse(responseText);
                for (var i = 0; i < obj.length; i++) {
                    var color = getRandomColor();
                    for (var j = obj[i].start; j <= obj[i].end; j++) {
                        var item = document.getElementById(j)
                        if (item.style.backgroundColor == '') {
                            item.style.backgroundColor = color;
                            item.title = obj[i].description;
                            console.log(obj[i].description);
                        } else {
                            item.style.backgroundColor = '';
                        }
                    }
                }
            }
        };

        xhr.open("GET", "work?tag=" + label.id, true);//提交get请求到服务器
        xhr.send(null)
    }

    function getRandomColor() {
        var rand = Math.floor(Math.random() * 0xFFFFFF).toString(16);
        if (rand.length == 6) {
            return rand;
        } else {
            var color = getRandomColor();
            if(color !='' && color != 'white')
                return color;
            else
                return getRandomColor();
        }
    }
</script>
