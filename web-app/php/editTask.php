<head>
    <title>Edit Task</title>
    <link rel = "stylesheet" type = "text/css" href = "main.css">
    <script>
        function deleteVerify() {
            var result = confirm("Are you sure you want to delete this task?");
            if (result == true) {
                window.location.href='./main.php'
            } else {

            }
        }
    </script>
</head>

<body>
    <h1 onclick="window.location.href='./main.php'">Web Scrape</h1>

    <span class="leftTopBar">
        <button type="button">Save</button>
    </span>

    <span class="rightTopBar">
        <i>Logged in as psyjct</i>
    </span>

    <div class="split left">
        <div style="margin:15px">
            <span class="selectedTaskName"><input type="text" placeholder="Task Name" value="Task A"></span>
            <span class="rightButtons">
                <button onclick="deleteVerify()">Delete</button>
            </span>
            <br><br>
            <input type="text" placeholder="Add description...">
            <br><br>
            Access by:
            <ul>
                <li>user <a>X</a></li>
                <li>group <a>X</a></li>
                <li>
                    <form>
                        <input type="text" placeholder="Add user/group...">
                        <button type="button">Add</button>
                    </form>
                </li>   
            </ul>
            <div class="taskURL"><input type="text" placeholder="Add URL" value="http://example.com"></div>

        </div>
    </div>

    <div class="split right" style="overflow: scroll;">
        <div style="margin:15px">
            Data to scrape:<br>
            <br>
            <table>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Element</th>
                    <th>Type</th>
                    <th></th>
                </tr>
                <tr>
                    <td>0</td>
                    <td>Page Title</td>
                    <td>&ltdiv id="title"&gt</td>
                    <td>String</td>
                    <td><a>X</a></td>
                </tr>
                <tr>
                    <td>1</td>
                    <td>Value</td>
                    <td>&ltdiv id="data-val"&gt</td>
                    <td>Integer</td>
                    <td><a>X</a></td>
                </tr>
            </table>
            <br>
            <i>Add new elements to scrape via the browser plugin</i>
        </div>
    </div>
</body>