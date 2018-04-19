<html>

<head>
    <meta charset="utf-8">
    <title>卖家登录页面</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.0.1/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <form action="/sell/seller/user/login">
        <div class="container">
            <div class="row clearfix">
                <div class="col-md-6 column">
                    <form role="form">
                        <div class="form-group">
                            <label for="exampleInputEmail1">用户登录名</label><input class="form-control" id="exampleInputEmail1" type="text" name="username" />
                        </div>
                        <div class="form-group">
                            <label for="exampleInputPassword1">用户密码</label><input class="form-control" id="exampleInputPassword1" type="password" name="password" />
                        </div>
                        <button type="submit" class="btn btn-default">登录</button>
                    </form>
                </div>
            </div>
        </div>
    </form>
</body>

</html>