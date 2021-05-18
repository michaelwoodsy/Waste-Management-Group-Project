/**
 * @jest-environment jsdom
 */

import "@jest/globals";
import {getTimeDiffStr} from '@/utils/dateTime'

// Unix epoch time for "2021-05-14T02:00:00Z"
const mockedUnixTime = 1620957600000;

// Mocking for the Date.now function
beforeEach(() => {
    // Mock the Date() function as 2021/15/14 at 2:00am
    Date.now = jest.fn(() => mockedUnixTime)
})

describe('Testing the dateTime getTimeAgo method', () => {

    test('Check a date minutes ago', () => {
        const mins32 = "2021-05-14T01:28:00Z"
        const mins1 = "2021-05-14T01:59:00Z"
        const mins59 = "2021-05-14T01:01:00Z"

        expect(getTimeDiffStr(mins32)).toBe("32m")
        expect(getTimeDiffStr(mins1)).toBe("1m")
        expect(getTimeDiffStr(mins59)).toBe("59m")
    })

    test('Check a date hours ago', () => {
        const hours4 = "2021-05-13T22:00:00Z"
        const hours5 = "2021-05-13T20:59:00Z"
        const hours23 = "2021-05-13T02:00:01Z"
        const hours1 = "2021-05-14T01:00:00Z"

        expect(getTimeDiffStr(hours4)).toBe("4h")
        expect(getTimeDiffStr(hours5)).toBe("5h")
        expect(getTimeDiffStr(hours23)).toBe("23h")
        expect(getTimeDiffStr(hours1)).toBe("1h")
    })

    test('Check a date days ago', () => {
        const days1 = "2021-05-13T01:00:00Z"
        const days3 = "2021-05-11T01:00:00Z"
        const days6 = "2021-05-08T01:00:00Z"

        expect(getTimeDiffStr(days1)).toBe("1d")
        expect(getTimeDiffStr(days3)).toBe("3d")
        expect(getTimeDiffStr(days6)).toBe("6d")
    })

    test('Check a date weeks ago', () => {
        const week1 = "2021-05-07T01:00:00Z"

        expect(getTimeDiffStr(week1)).toBe("1w")
    })

    test('Check a date in future', () => {
        const futureDate = "2021-06-14T01:28:00Z"

        expect(getTimeDiffStr(futureDate)).toBe("now")
    })

})
