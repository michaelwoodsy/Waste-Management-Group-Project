/**
 * Functionality related to undo operations.
 */
import Vue from "vue";


export default {

    /** Data needed to run / undo operations **/
    state: Vue.observable({
        toDelete: null,
        timedMethod: null
    }),

    /**
     * Queues a notification request to be run in the future so it can be undone.
     * @param request Handler / function to run when the undo period has passed.
     * @param notification The notification being deleted in the request.
     * @param undoHandle Method to run when undo is called
     */
    queueNotificationDelete(request, notification, undoHandle) {
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
        this.state.toDelete = {request, notification, undoHandle}
        this.state.timedMethod = setTimeout(async () => {
            await this.state.toDelete.request()
            // TODO: Error handling
            this.state.toDelete = null
        }, 10000);
    },

    /**
     * Cancels the current delete request.
     */
    cancelDelete() {
        window.addEventListener('beforeunload', () => {
            // clear unload event
        })

        clearTimeout(this.state.timedMethod)
        this.state.toDelete.undoHandle()
        this.state.toDelete = null
    },

    /**
     * True if there is an operation that can be undone
     * @returns {boolean} True if there is a pending undo operation
     */
    canUndo() {
        return this.state.toDelete !== null
    }

}