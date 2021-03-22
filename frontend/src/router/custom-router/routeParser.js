

/**
 * Find a route from a list of routes
 * @param routes List of route objects
 * @param routeInfo route we want to find, either string or object
 * @returns {void|*}
 */
export const findRoute = (routes, routeInfo) => {
    const route = routes.find(route => route.parsedRoute.isSameRoute(routeInfo));
    if (route) {
        return route.parsedRoute.getRouteObject(routeInfo)
    } else {
        return null
    }

};


export class ParsedRoute {
    constructor (route) {
        this.path = route.path;
        this.query = route.query;
        this.name = route.name;
        this.component = route.component;
        this.pathParts = this.getPathParts(route.path)
    }

    /**
     * Returns a list of path parts
     * @param pathStr String representation of a path
     * @returns {[]}
     */
    getPathParts (pathStr) {
        // Parse path params
        let pathParts = [];
        pathStr.split('/').forEach(part => {
            if (part !== '') {
                // If part of path is a param
                if (part.charAt(0) === ':') {
                    pathParts.push({
                        isParam: true,
                        name: part.slice(1)
                    })
                } else {
                    pathParts.push({
                        isParam: false,
                        name: part
                    })
                }
            }
        });
        return pathParts
    }

    /**
     * Check if a path belongs to this route
     * @param path String representation of a path or an object representation
     */
    isSameRoute (path) {
        if (typeof path === 'object') {
            if (typeof path.name === 'string') {
                return path.name === this.name
            }
        } else if (typeof path === 'string') {
            const pathParts = this.getPathParts(path);
            if (pathParts.length === this.pathParts.length) {
                // Iterate over the path parts and return false if they don't match
                for (let i = 0; i < pathParts.length; i++) {
                    if (!this.pathParts[i].isParam && this.pathParts[i].name !== pathParts[i].name) {
                        return false
                    }
                }
                return true
            }
            return false
        } else {
            return false
        }
    }

    /**
     * Returns a route object
     * @param path String representation of a path or an object representation
     */
    getRouteObject (path) {
        if (typeof path === 'object') {
            return this.buildRouteFromParams(path)

        } else if (typeof path === 'string') {
            return this.buildRouteFromPath(path)
        }
        return null

    }

    /**
     * Builds a route object from a path string
     * @param path
     * @returns {{}}
     */
    buildRouteFromPath (path) {
        let route = {};
        route.name = this.name;
        route.params = {};
        route.query = {};
        route.path = path;
        route.component = this.component;

        const pathParts = this.getPathParts(path);

        // Add path params to the route object
        for (let i = 0; i < pathParts.length; i++) {
            if (this.pathParts[i].isParam) {
                route.params[this.pathParts[i].name] = pathParts[i].name
            }
        }

        // TODO: Add query params to the route object

        return route
    }

    /**
     * Builds a route from path object
     * @param path
     * @returns {{}}
     */
    buildRouteFromParams (path) {
        let route = {};

        route.name = this.name;
        route.params = {...path.params};
        route.query = {...path.query};
        route.path = '';
        route.component = this.component;

        // Add path params
        this.pathParts.forEach(part => {
            if (part.isParam) {
                route.path += `/${route.params[part.name]}`;
            } else {
                route.path += `/${part.name}`
            }
        });

        // Add query params
        if (Object.keys(route.query).length !== 0 && route.query.constructor === Object) {
            route.path += '?';
            let isFirst = true;
            Object.keys(route.query).forEach(key => {
                if (!isFirst) {
                    route.path += '&'
                }
                route.path += `${key}=${route.query[key]}`;
                isFirst = false
            })
        }

        return route
    }

}


