<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="d-none d-sm-block col-sm-1"></div>
        <div class="col-12 col-sm-10">

            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarText">
                <ul class="navbar-nav mr-auto">
                    <li class="nav-item active">
                        <router-link class="nav-link" to="/">Home</router-link>
                    </li>
                    <li class="nav-item" v-if="isLoggedIn">
                        <router-link class="nav-link" to="/users">Users</router-link>
                    </li>
                </ul>
                <span class="navbar-text">
                    <span class="float-right d-inline" v-if="isLoggedIn">
                        <router-link class="nav-link d-inline" @click.native="logOut()" to="/">Logout</router-link> /
                        <router-link class="nav-link d-inline" to="/profile">Profile</router-link>
                    </span>
                        <span class="float-right d-inline" v-else>
                        <router-link class="nav-link d-inline" to="/login">Login</router-link> /
                        <router-link class="nav-link d-inline" to="/register">Register</router-link>
                    </span>
                </span>
            </div>

        </div>
        <div class="d-none d-sm-block col-sm-1"></div>

    </nav>

</template>

<script>
    export default {
        name: "Navbar",
        computed: {
          isLoggedIn () {
            return this.$root.$data.user.state.loggedIn

          }
        },
        methods: {
          logOut() {
            this.$root.$data.user.logout();
          }
        }
    }
</script>

<style scoped>

</style>
