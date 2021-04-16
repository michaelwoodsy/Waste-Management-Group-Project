const listeners = [];

// Push function for navigating routes
export const push = (route) => {
    const prevRoute = window.location.pathname;
    window.history.pushState(null, null, route);
    listeners.forEach(func => func(route, prevRoute))
};

export const listen = (func) => {
    listeners.push(func)
};
