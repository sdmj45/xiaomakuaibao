#!/bin/bash
function lazygit() {
    git add public
    git commit -m "site updated"
    git subtree push --prefix=public  https://github.com/sdmj45/xiaomakuaibao.git master
    git push
}
lazygit