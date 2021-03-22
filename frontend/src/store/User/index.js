import { User } from '../../Api'

export default {
    debug: true,
    state: {
        message: 'Hello!',
        loggedIn: false,
        userId: null
    },
    login (username, password) {
        return new Promise((resolve, reject) => {
            User.login(username, password)
                .then((res) => {
                    this.state.loggedIn = true;
                    this.state.userId = res.data.userId;
                    resolve(res)
                })
                .catch((err) => {
                    this.state.loggedIn = false;
                    this.state.userId = null;
                    reject(err)
                })
        })
    },
    logout () {
        return new Promise((resolve) => {
            this.state.loggedIn = false;
            this.state.userId = null;
            resolve({})
        })
    }
}
