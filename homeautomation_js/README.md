# A example of home automation with Javascript code
## Preparation of home automation
1. Install apache webserver
```
$ sudo apt install apache2
Reading package lists... Done
Building dependency tree       
Reading state information... Done

$ curl -v localhost | head -1
* Rebuilt URL to: localhost/
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0*   Trying 127.0.0.1...
* Connected to localhost (127.0.0.1) port 80 (#0)
> GET / HTTP/1.1
> Host: localhost
> User-Agent: curl/7.47.0
> Accept: */*
>
< HTTP/1.1 200 OK

```

2. Install node framework
```
$ sudo apt install npm
$ npm -v
3.5.2
```

Install node via `nodenv` and write following contents to `~/.bash_profile`

```
$ git clone git://github.com/nodenv/nodenv.git ~/.nodenv

$ echo 'export PATH="$HOME/.nodenv/bin:$PATH"' >> ~/.bash_profile
$ echo 'eval "$(nodenv init -)"' >> ~/.bash_profile
```

Set the path to `~/.bashrc` for using node.

```
#nodeenv
if [ -f ~/.bash_profile ]; then
    . ~/.bash_profile
fi

$ nodenv -v
nodenv 1.1.2-1-g18489d7
```

Install plugin

```
$ git clone https://github.com/nodenv/node-build.git ~/.nodenv/plugins/node-build
```

Install latest version `node` via nodenv

```
$ nodenv install -l
Available versions:
  0.1.14
  0.1.15
  0.1.16
  0.1.17
...
  9.0.0
  9.1.0
  9.2.0
  chakracore-dev
...
  jxcore+v8-0.3.1.1
  nightly
  node-dev
  rc
  v8-canary
```

Set latest version

```
$ nodenv global 9.2.0
$ node -v
v9.2.0
```

Update npm

```
$ npm install -g npm
$ npm -v
5.6.0
```

## `index.html`
http://xxx.xxx.xxx.xxx/mqttTraining/homeautomation_js/automationView.html
