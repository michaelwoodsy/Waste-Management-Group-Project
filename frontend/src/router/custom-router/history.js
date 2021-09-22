const listeners = [];
import {findRoute} from "./routeParser";

// Push function for navigating routes
export const push = (route, routes) => {
    const prevRoute = window.location.pathname;
    const prevRouteObj = findRoute(routes, window.location.pathname);

    const next = (cont = true) => {
        // If the route guard chooses to continues
        if (cont) {
            window.history.pushState(null, null, route);
            listeners.forEach(func => func(route, prevRoute))
        }
    }

    // Check the before leave guard
    if (prevRouteObj && prevRouteObj.component.beforeRouteLeave) {
        prevRouteObj.component.beforeRouteLeave(route, prevRoute, next)
    } else {
        next()
    }
};

export const runRouteGuard = (route, parsedRoutes) => {
    const prevRoute = route;
    const prevRouteObj = findRoute(parsedRoutes, prevRoute);

    // Check the before leave guard
    if (prevRouteObj && prevRouteObj.component.beforeRouteLeave) {
        prevRouteObj.component.beforeRouteLeave(route, prevRoute, () => {})
    }
}

export const listen = (func) => {
    listeners.push(func)
};
