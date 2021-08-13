import "@jest/globals"
import undo from '@/utils/undo'

describe('Testing the undo module', () => {

    test('Test the queueDelete method sets onUnload event listener', () => {
        jest.spyOn(window, 'addEventListener')
        undo.queueDelete()
        expect(window.addEventListener).toBeCalledWith('beforeunload', expect.any(Function));
    })

    test('Test the queueDelete method sets the toDelete property', () => {
        let request = () => {}
        undo.queueDelete(request, {}, "type", request)
        expect(undo.state.toDelete.request).toBe(request)
        expect(undo.state.toDelete.type).toBe("type")
        expect(undo.state.toDelete.undoHandle).toBe(request)
    })
    test('Test the queueDelete method runs request after 10 seconds', () => {
        jest.useFakeTimers();
        let request = jest.fn()
        undo.queueDelete(request, {}, "type", request)
        jest.runAllTimers();
        expect(request).toBeCalled();
        jest.useRealTimers();
    })

    test('Test the queueNotificationDelete method calls the queueDelete method', () => {
        jest.spyOn(undo, 'queueDelete')
        let request = jest.fn()
        let handle = jest.fn()
        let notification = {}
        undo.queueNotificationDelete(request, notification, handle)
        expect(undo.queueDelete).toBeCalledWith(request, notification, "Notification", handle)
    })

    test('Test the queueMessageDelete method calls the queueDelete method', () => {
        jest.spyOn(undo, 'queueDelete')
        let request = jest.fn()
        let handle = jest.fn()
        let message = {}
        undo.queueMessageDelete(request, message, handle)
        expect(undo.queueDelete).toBeCalledWith(request, message, "Message", handle)
    })

    test('Test the canUndo method returns true when there is a request to undo', () => {
        undo.state.toDelete = {request: jest.fn()}
        expect(undo.canUndo()).toBeTruthy()
    })

    test('Test the canUndo method returns false when there is no request to undo', () => {
        undo.state.toDelete = null
        expect(undo.canUndo()).toBeFalsy()
    })

    test('Test the isMessageRequest method returns true when the request is for a message', () => {
        undo.state.toDelete = {type: "Message"}
        expect(undo.isMessageRequest()).toBeTruthy()
    })

    test('Test the isMessageRequest method returns false when the request is for a notification', () => {
        undo.state.toDelete = {type: "Notification"}
        expect(undo.isMessageRequest()).toBeFalsy()
    })

    test('Test the cancelDelete method clears the onUnload event listener', () => {
        jest.spyOn(window, 'addEventListener')
        undo.queueDelete()
        expect(window.addEventListener).toBeCalledWith('beforeunload', expect.any(Function));
    })

    test('Test the cancelDelete method cancels the future request', () => {
        jest.useFakeTimers()
        let timerMethod = jest.fn()
        undo.state.timedMethod = timerMethod
        undo.cancelDelete()
        expect(clearTimeout).toBeCalledWith(timerMethod);
        jest.useFakeTimers()
    })

})