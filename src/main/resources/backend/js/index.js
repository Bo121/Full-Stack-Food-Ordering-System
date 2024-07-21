/* Custom trim function */
function trim(str) {  // Remove whitespace from both ends, custom trim() method
    return str == undefined ? "" : str.replace(/(^\s*)|(\s*$)/g, "")
}

// Get URL parameters
function requestUrlParam(argname) {
    var url = location.href; // Get the complete request URL path
    var arrStr = url.substring(url.indexOf("?") + 1).split("&");
    for (var i = 0; i < arrStr.length; i++) {
        var loc = arrStr[i].indexOf(argname + "=");
        if (loc != -1) {
            return arrStr[i].replace(argname + "=", "").replace("?", "");
        }
    }
    return "";
}
