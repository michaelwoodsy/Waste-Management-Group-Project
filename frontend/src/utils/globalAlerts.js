/** Module for creating & watching for global alerts **/

import Vue from 'vue';

const eventBus = new Vue();

/**
 * Adds a callback to run when an alert is triggered.
 * @param callback Function to run when an alert is created.
 */
export function listen(callback) {
    eventBus.$on("globalAlert", callback)
}

/**
 * Emits a global alert through the eventBus
 * @param message Message for the alert
 */
export function createRed(message) {
    eventBus.$emit("globalAlert", message)
}
