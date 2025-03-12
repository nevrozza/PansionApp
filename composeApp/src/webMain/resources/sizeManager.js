class SizeManager {
    constructor() {
        this._changes = [];
        this.resize = this.resize.bind(this);

        if (window.visualViewport) {
            window.visualViewport.onresize = this.resize;
        }

        this.resize();
    }

    getChanges() {
        return new Promise((resolve) => {
            if (this._changes.length > 0) {
                resolve(this._changes.shift());
            } else {
                const listener = (event) => {
                    resolve(event.detail);
                    window.removeEventListener('sizeChange', listener);
                };
                window.addEventListener('sizeChange', listener);
            }
        });
    }

    resize() {
        const newSize = {
            width: window.innerWidth,
            height: window.visualViewport ? window.visualViewport.height : window.innerHeight
        };
        this._changes.push(newSize);
        window.dispatchEvent(new CustomEvent('sizeChange', { detail: newSize }));
    }
}