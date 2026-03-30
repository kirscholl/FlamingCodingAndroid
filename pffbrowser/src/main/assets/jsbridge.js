/**
 * JSBridge SDK for H5
 * 提供与Native通信的JavaScript接口
 */
(function(window) {
    'use strict';

    // 检查是否已经初始化
    if (window.JSBridge) {
        return;
    }

    // 回调ID计数器
    let callbackId = 0;

    // 回调映射表
    const callbacks = {};

    // 事件监听器映射表
    const eventListeners = {};

    // 是否就绪
    let isReady = false;
    const readyCallbacks = [];

    /**
     * 生成唯一的回调ID
     */
    function generateCallbackId() {
        return 'cb_' + (++callbackId) + '_' + Date.now();
    }

    /**
     * 调用Native方法
     * @param {string} module - 模块名
     * @param {string} method - 方法名
     * @param {object} params - 参数
     * @returns {Promise}
     */
    function callNative(module, method, params = {}) {
        return new Promise((resolve, reject) => {
            if (!window.AndroidJSBridge) {
                reject({
                    code: 9999,
                    message: 'JSBridge未初始化'
                });
                return;
            }

            const cbId = generateCallbackId();

            // 保存回调
            callbacks[cbId] = { resolve, reject };

            // 构造请求消息
            const message = JSON.stringify({
                callbackId: cbId,
                module: module,
                method: method,
                params: params,
                timestamp: Date.now()
            });

            try {
                // 调用Native方法
                window.AndroidJSBridge.call(message);
            } catch (e) {
                delete callbacks[cbId];
                reject({
                    code: 9999,
                    message: e.message || '调用失败'
                });
            }
        });
    }

    /**
     * Native回调处理函数
     * @param {string} responseJson - 响应JSON字符串
     */
    window._jsbridge_callback = function(responseJson) {
        try {
            const response = typeof responseJson === 'string'
                ? JSON.parse(responseJson)
                : responseJson;

            const callback = callbacks[response.callbackId];
            if (!callback) {
                console.warn('Callback not found:', response.callbackId);
                return;
            }

            // 删除回调
            delete callbacks[response.callbackId];

            // 执行回调
            if (response.code === 0) {
                callback.resolve(response.data);
            } else {
                callback.reject({
                    code: response.code,
                    message: response.message
                });
            }
        } catch (e) {
            console.error('JSBridge callback error:', e);
        }
    };

    /**
     * Native事件处理函数
     * @param {string} eventJson - 事件JSON字符串
     */
    window._jsbridge_event = function(eventJson) {
        try {
            const event = typeof eventJson === 'string'
                ? JSON.parse(eventJson)
                : eventJson;

            const listeners = eventListeners[event.event];
            if (listeners && listeners.length > 0) {
                listeners.forEach(listener => {
                    try {
                        listener(event.data);
                    } catch (e) {
                        console.error('Event listener error:', e);
                    }
                });
            }
        } catch (e) {
            console.error('JSBridge event error:', e);
        }
    };

    /**
     * 注册事件监听
     * @param {string} event - 事件名
     * @param {function} callback - 回调函数
     */
    function on(event, callback) {
        if (!eventListeners[event]) {
            eventListeners[event] = [];
        }
        eventListeners[event].push(callback);
    }

    /**
     * 取消事件监听
     * @param {string} event - 事件名
     * @param {function} callback - 回调函数
     */
    function off(event, callback) {
        const listeners = eventListeners[event];
        if (!listeners) {
            return;
        }

        if (!callback) {
            // 如果没有指定回调，删除所有监听器
            delete eventListeners[event];
        } else {
            // 删除指定的监听器
            const index = listeners.indexOf(callback);
            if (index > -1) {
                listeners.splice(index, 1);
            }
        }
    }

    /**
     * JSBridge就绪回调
     * @param {function} callback - 回调函数
     */
    function ready(callback) {
        if (isReady) {
            callback();
        } else {
            readyCallbacks.push(callback);
        }
    }

    // 初始化完成
    setTimeout(() => {
        isReady = true;
        readyCallbacks.forEach(callback => {
            try {
                callback();
            } catch (e) {
                console.error('Ready callback error:', e);
            }
        });
        readyCallbacks.length = 0;
    }, 0);

    // 导出JSBridge对象
    window.JSBridge = {
        callNative: callNative,
        on: on,
        off: off,
        ready: ready,

        // 便捷方法
        ui: {
            showToast: (message, duration = 2000) =>
                callNative('ui', 'showToast', { message, duration }),
            showDialog: (title, message, buttons = ['确定']) =>
                callNative('ui', 'showDialog', { title, message, buttons }),
            vibrate: (duration = 100) =>
                callNative('ui', 'vibrate', { duration })
        },

        storage: {
            setItem: (key, value) =>
                callNative('storage', 'setItem', { key, value }),
            getItem: (key) =>
                callNative('storage', 'getItem', { key }),
            removeItem: (key) =>
                callNative('storage', 'removeItem', { key }),
            clear: () =>
                callNative('storage', 'clear', {}),
            getAllKeys: () =>
                callNative('storage', 'getAllKeys', {})
        },

        device: {
            getDeviceInfo: () =>
                callNative('device', 'getDeviceInfo', {}),
            getAppInfo: () =>
                callNative('device', 'getAppInfo', {}),
            getSystemInfo: () =>
                callNative('device', 'getSystemInfo', {})
        },

        network: {
            request: (url, method = 'GET', headers = {}, body = '') =>
                callNative('network', 'request', { url, method, headers, body }),
            getNetworkType: () =>
                callNative('network', 'getNetworkType', {}),
            isNetworkAvailable: () =>
                callNative('network', 'isNetworkAvailable', {})
        },

        navigation: {
            push: (url) =>
                callNative('navigation', 'push', { url }),
            pop: () =>
                callNative('navigation', 'pop', {}),
            reload: () =>
                callNative('navigation', 'reload', {}),
            openNewWindow: (url) =>
                callNative('navigation', 'openNewWindow', { url }),
            close: () =>
                callNative('navigation', 'close', {})
        }
    };

    console.log('JSBridge initialized');

})(window);
