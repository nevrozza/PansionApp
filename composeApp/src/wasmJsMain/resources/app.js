function onLoadFinished() {
    document.dispatchEvent(new Event("app-loaded"));
}

document.addEventListener("app-loaded", function() {
    document.getElementById("spinner").style.display = "none";
});

class SizeManager {
    constructor() {
        this._changesEvent = new CustomEvent('sizechange', { detail: {} });

        window.visualViewport.addEventListener('resize', this.resize.bind(this));
    }

    resize() {
        const size = {
            width: window.innerWidth,
            height: window.visualViewport.height
        };

        this._changesEvent = new CustomEvent('sizechange', { detail: size });
        window.dispatchEvent(this._changesEvent);
    }

    get changes() {
        return {
            subscribe: (callback) => {
                window.addEventListener('sizechange', (event) => {
                    callback(event.detail);
                });
            }
        };
    }
}