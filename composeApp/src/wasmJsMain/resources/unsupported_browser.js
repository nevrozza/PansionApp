const unhandledError = (event, error) => {
    // Hide Webpack Overlay
    const webpackOverlay = document.getElementById("webpack-dev-server-client-overlay");
    if (webpackOverlay != null) {
      webpackOverlay.style.display = "none";
    }
    if (error instanceof WebAssembly.CompileError) {
        document.getElementById("warning unsupportError").style.display = "initial";
    }
    else {
        document.getElementById("warning unknownError").style.display = "initial";
    }

    document.getElementById("spinner").style.display = "none";
    document.getElementById("composeApp").style.display = "none";
}
addEventListener("error", (event) => unhandledError(event, event.error));
addEventListener("unhandledrejection", (event) => unhandledError(event, event.reason));