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

        let parsedRoutes = parseRouteParams([...options.routes]);

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
            }
        };
        // Set current route as an observable
        let route = Vue.observable({cur: findRoute(parsedRoutes, window.location.pathname)})
        Object.defineProperty(Vue.prototype, '$route', {
            get () {
                return route.cur
            },
            set (value) {
                route.cur = value
            }
        })

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
const parseRouteParams = (routes) => {
    routes.forEach(route => {
        route.parsedRoute = new ParsedRoute(route)
    });
    return routes
};
