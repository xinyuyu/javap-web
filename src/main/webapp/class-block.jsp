<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>class-block</title>
    <style>
        .item {
            height: 30px;
            width: 30px;
            border: 1px solid blue;
            float: left;
            text-align: center;
        }

        .content {
            float: left;
            width: 45%;
        }

        .log {
            position: absolute;
            right: 20px;
            top: 25px;
            width: 55%;
            height: 600px;
            border: 3px solid blue;
        }

        .color-block {
            height: 25px;
            width: 100%;
        }

        .upload {
            position: absolute;
            right: 20px;
            top: 0px;
            width: 55%;
            height: 15px;
        }
    </style>
</head>

<body>
<div id="content" class="content">
</div>
<div id="log" class="log">
</div>
<div id="upload" class="upload">
    <form method="post" action="upload" enctype="multipart/form-data">
        选择一个Class文件:
        <input type="file" name="uploadFile"/>
        <input type="submit" value="上传"/>
    </form>

</div>
<!--
<footer style="background-color: deepskyblue; text-align: center; color: white; height: 25px">
    <p>Create by yuyu</p>
</footer>
-->
<script src="https://cdn.bootcss.com/jsPlumb/2.6.8/js/jsplumb.min.js"></script>
<script src="https://cdn.bootcss.com/jquery/1.10.2/jquery.min.js"></script>

<script>
    var errorMsg = '${error}';
    var path = '${path}';
    if (errorMsg != 'suc' && errorMsg != '')
        alert(errorMsg);
    var content = "";
    var COLUMN_SIZE = 15;
    var classItems = ${content};
    var blocksStart = ${blocksStart};
    var blockStartArray = new Array();
    for (var i = 0; i < blocksStart.length; i++) {
        blockStartArray[blocksStart[i].start] = '1';
    }

    var rowSize = classItems.length / COLUMN_SIZE + 1;
    for (var i = 0; i < rowSize; i++) {
        //var rowContent = "<div id=row_" + i + ">";
        var rowContent = "";
        for (var j = 0; j < COLUMN_SIZE; j++) {
            var index = j + i * COLUMN_SIZE;
            var code = classItems[index];
            if (code != undefined) {
                if (blockStartArray[index] == 1) {
                    var itemContent = "<div id='" + index + "' class='item' style='margin-left:15px; color: orangered' onclick='show(this)'>" + code + '</div>';
                } else {
                    var itemContent = "<div id='" + index + "' class='item' style='margin-left:15px;'>" + code + '</div>';
                }
                rowContent += itemContent;
            }
        }
        rowContent += "</div><br /><br />"
        content += rowContent;
    }
    document.getElementById("content").innerHTML = content;

    function getRandomColor() {
        return '#' +
            (function (color) {
                return (color += '0123456789abcdef'[Math.floor(Math.random() * 16)])
                && (color.length == 6) ? color : arguments.callee(color);
            })('');
    }


    var jp = jsPlumb.getInstance();
    jp.setContainer(document.getElementById("content"));

    function show(label) {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
                var responseText = xhr.responseText;
                var rangeList = JSON.parse(responseText);
                var logContent = '';
                for (var i = 0; i < rangeList.length; i++) {
                    var color = getRandomColor();
                    var parent = rangeList[i].parent;
                    if (parent != -1) {
                        jp.connect({
                            source: parent + '',
                            target: rangeList[i].start + '',
                            endpoint: 'Blank',
                            overlays: [['Arrow', {width: 15, length: 15, location: 1}]],
                            paintStyle: {stroke: 'lightgray', strokeWidth: 3},
                            endpointStyle: {fill: 'lightgray', outlineStroke: 'darkgray', outlineWidth: 2},
                            reattach: true
                        });
                    }
                    $("#log").html('');
                    logContent += '<div class="color-block" style="background-color:' + color + '">' + rangeList[i].description + '</div>';
                    for (var j = rangeList[i].start; j <= rangeList[i].end; j++) {
                        var item = document.getElementById(j)
                        if (item.style.backgroundColor == "") {
                            item.style.backgroundColor = color;
                            item.title = rangeList[i].description;
                            $("#log").html(logContent);
                        } else {
                            item.style.backgroundColor = "";
                            jp.deleteConnectionsForElement(item.getAttribute("id") + "");
                        }
                    }
                }
            }
        };

        xhr.open("GET", "work?offset=" + label.id + '&path=' + encodeURIComponent(encodeURIComponent(path)), true);//提交get请求到服务器
        xhr.send(null)
    }

    $(document).ready(function () {
        var menuYloc = $("#log").offset().top;
        $(window).scroll(function () {
            var offsetTop = menuYloc + $(window).scrollTop() + "px";
            $("#log").animate({top: offsetTop}, {duration: 0, queue: false});
        });
    });


</script>
</body>

</html>
