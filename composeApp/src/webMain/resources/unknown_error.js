const unhandledError = (event, error) => {
        document.getElementById("warning unknownError").style.display = "initial";
        document.getElementById("spinner").style.display = "none";
        document.getElementById("composeApp").style.display = "none";
}
addEventListener("error", (event) => unhandledError(event, event.error));
addEventListener("unhandledrejection", (event) => unhandledError(event, event.reason));