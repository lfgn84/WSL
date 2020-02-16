function myFunction() {
        document.getElementById("test").style.cursor = "wait";
        setTimeout('document.getElementById("test").style.cursor = "pointer"', 1000);
    }