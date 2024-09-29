function onLoadFinished() {
    document.dispatchEvent(new Event("app-loaded"));
}

document.addEventListener("app-loaded", function() {
    document.getElementById("spinner").style.display = "none";
});

if ("virtualKeyboard" in navigator) {
console.log(navigator.virtualKeyboard.overlaysContent); // false
navigator.virtualKeyboard.overlaysContent = true; // Opt out of the automatic handling.
}

