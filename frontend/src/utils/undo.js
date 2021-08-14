/**
 * Functionality related to undo operations.
 */
import Vue from "vue";


export default {

    /** Data needed to undo operations **/
    state: Vue.observable({
        toDelete: null,
        timedMethod: null
    }),

    /**
     * Queues a delete request to be run in the future so it can be undone.
     * @param request Handler / function to run when the undo period has passed.
     * @param data The data of the thing being deleted in the request.
     * @param undoHandle Method to run when undo is called
     * @param type String type of thing being deleted (eg. 'Message')
     */
    queueDelete(request, data, type, undoHandle) {
        // check if there is an existing operation not completed yet
        if (this.canUndo()) {
            this.state.toDelete.request()
            clearTimeout(this.state.timedMethod)
        }

        // add the event handler for if the window is closed
        window.addEventListener('beforeunload', () => {
            this.state.toDelete.request()
            return "You have some unsaved changes";
        })

        // set the new undo request
        this.state.toDelete = {request, data, undoHandle, type}
        this.state.timedMethod = setTimeout(async () => {
            await this.state.toDelete.request()
            this.state.toDelete = null
        }, 10000);
    },

    /**
     * Queues a notification request to be run in the future so it can be undone.
     * @param request Handler / function to run when the undo period has passed.
     * @param notification The notification being deleted in the request.
     * @param undoHandle Method to run when undo is called
     */
    queueNotificationDelete(request, notification, undoHandle=null) {
        this.queueDelete(request, notification, 'Notification', undoHandle)
    },

    /**
     * Queues a message request to be run in the future so it can be undone.
     * @param request Handler / function to run when the undo period has passed.
     * @param message The message being deleted in the request.
     * @param undoHandle Method to run when undo is called
     */
    queueMessageDelete(request, message, undoHandle=null) {
        this.queueDelete(request, message, 'Message', undoHandle)
    },

    /**
     * Cancels the current delete request.
     */
    cancelDelete() {
        window.addEventListener('beforeunload', () => {
            // clear unload event
        })

        clearTimeout(this.state.timedMethod)
        // run the undoHandle if there is one
        if (this.state.toDelete && this.state.toDelete.undoHandle) {this.state.toDelete.undoHandle()}
        this.state.toDelete = null
    },

    /**
     * True if there is an operation that can be undone
     * @returns {boolean} True if there is a pending undo operation
     */
    canUndo() {
        return this.state.toDelete !== null
    },

    /**
     * Returns true if the queued deletion is for a message.
     * @returns {boolean} True if the queued delete is for a message.
     */
    isMessageRequest() {
        return this.state.toDelete.type === 'Message'
    }

}