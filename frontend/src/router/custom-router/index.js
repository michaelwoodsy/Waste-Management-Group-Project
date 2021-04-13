import Router from './Router'
import RouterLink from './RouterLink'
import { push as historyPush } from './history'
import { findRoute, ParsedRoute } from "./routeParser";
import { listen } from "./history";

export default {
    // called by Vue.use()
    install(Vue, options) {

        // Define the global components
        Vue.component('RouterView', Router);
        Vue.component('RouterLink', RouterLink);

        // Parse the routes
        let parsedRoutes = parseRouteParams([...options.routes], options.base);

        // Define routes globally
        Vue.prototype.$routes = parsedRoutes;
        Vue.prototype.$router = {
            push: (route) => {
                let found = findRoute(parsedRoutes, route);
                if (found) {
                    historyPush(found.path)
                } else {
                    historyPush(route.path || route)
                }
            },
            base: options.base
        };
        Vue.prototype.$route = findRoute(parsedRoutes, window.location.pathname);

        // Create listeners to update $route parameter on update
        listen((newRoute) => {
            Vue.prototype.$route = findRoute(parsedRoutes, newRoute)
        });
        window.addEventListener(
            'popstate',
            () => {
                Vue.prototype.$route = findRoute(parsedRoutes, window.location.pathname)
            })

    }
}

/**
 * Parse parameters and add them in place
 * @param routes Routes object
 */
const parseRouteParams = (routes, base) => {
    routes.forEach(route => {
        route.parsedRoute = new ParsedRoute(route, base)
    });
    return routes
};
