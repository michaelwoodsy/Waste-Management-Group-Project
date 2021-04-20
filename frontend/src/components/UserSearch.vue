<template>
    <div>
        <div class="container-fluid" v-if="isLoggedIn">

            <div class="row">
                <div class="col-12 text-center mb-2">
                    <h4>Search Users</h4>
                </div>
            </div>

            <div v-if="error" class="row">
                <div class="col-8 offset-2 text-center mb-2">
                    <alert>error</alert>
                </div>
            </div>

            <div class="row mb-2">
                <div class="col-12 col-sm-8 col-lg-6 col-xl-4 col-centered">
                    <div class="input-group">
                        <input type="search"
                               id="search"
                               class="form-control no-outline"
                               placeholder="name or nickname"
                               @keyup.enter="search"
                               v-model="searchTerm">
                        <div class="input-group-append">
                             <button class="btn btn-primary no-outline" type="button" @click="search">Search</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="d-none d-lg-block col-lg-1"/>
                <div class="col-12 col-lg-10">
                    <div class="text-center">
                        <showing-results-text
                            :items-per-page="resultsPerPage"
                            :page="page"
                            :total-count="totalCount"
                        />
                    </div>

                    <div class="overflow-auto">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th scope="col" class="pointer" @click="orderResults('id')">
                                    <p class="d-inline">Id</p>
                                    <p class="d-inline" v-if="orderCol === 'id'">{{ orderDirArrow }}</p>
                                </th>

                                <th scope="col" class="pointer" @click="orderResults('firstName')">
                                    <p class="d-inline">Firstname</p>
                                    <p class="d-inline" v-if="orderCol === 'firstName'">{{ orderDirArrow }}</p>
                                </th>

                                <th scope="col" class="pointer" @click="orderResults('middleName')">
                                    <p class="d-inline">Middlename</p>
                                    <p class="d-inline" v-if="orderCol === 'middleName'">{{ orderDirArrow }}</p>
                                </th>

                                <th scope="col"  class="pointer" @click="orderResults('lastName')">
                                    <p class="d-inline">Lastname</p>
                                    <p class="d-inline" v-if="orderCol === 'lastName'">{{ orderDirArrow }}</p>
                                </th>

                                <th scope="col" class="pointer" @click="orderResults('email')">
                                    <p class="d-inline">Email</p>
                                    <p class="d-inline" v-if="orderCol === 'email'">{{ orderDirArrow }}</p>
                                </th>

                                <th scope="col" class="pointer" @click="orderResults('homeAddress')">
                                    <p class="d-inline">Address</p>
                                    <p class="d-inline" v-if="orderCol === 'homeAddress'">{{ orderDirArrow }}</p>
                                </th>
                            </tr>
                            </thead>
                            <tbody v-if="!loading">
                                <tr v-bind:key="user.id"
                                    v-for="user in paginatedUsers"
                                    @click="viewUser(user.id)"
                                    class="pointer"
                                >
                                    <th scope="row">{{ user.id }}</th>
                                    <td>{{ nameAndNickname(user) }}</td>
                                    <td>{{ user.middleName }}</td>
                                    <td>{{ user.lastName }}</td>
                                    <td>{{ user.email }}</td>
                                    <td>{{ formattedAddress(user.homeAddress) }}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="d-none d-lg-block col-lg-1"/>
            </div>

            <div class="row" v-if="loading">
                <div class="col-12 text-center">
                    <p class="text-muted">Loading...</p>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <pagination
                        :total-items="totalCount"
                        :current-page.sync="page"
                        :items-per-page="resultsPerPage"
                        class="mx-auto"
                    />
                </div>
            </div>
        </div>

        <login-required v-else page="search users"/>
    </div>
</template>

<script>
    import { User } from '../Api'
    import LoginRequired from './LoginRequired'
    import ShowingResultsText from "./ShowingResultsText";
    import Pagination from "./Pagination";
    import Alert from './Alert'

    export default {
        name: "UserSearch",
        components: {
            LoginRequired,
            Alert,
            ShowingResultsText,
            Pagination
        },
        data () {
            return {
                searchTerm: null,
                users: [],
                error: null,
                orderCol: null,
                orderDirection: false, // False -> Ascending
                resultsPerPage: 10,
                page: 1,
                loading: false
            }
        },
        computed: {
            isLoggedIn () {
                return this.$root.$data.user.state.loggedIn
            },
            orderDirArrow () {
                if (this.orderDirection) {
                    return '↓'
                }
                return '↑'
            },
            sortedUsers () {
                if (this.orderCol === null) {
                    return this.users
                }

                // Create new users array and sort
                let newUsers = [...this.users];
                // Order direction multiplier for sorting
                const orderDir = (this.orderDirection ? 1 : -1);

                // Sort users if there are any
                if (newUsers.length > 0) {
                    // Sort users
                    newUsers.sort((a, b) => {
                        return orderDir * this.sortAlpha(a, b)
                    });
                }

                return newUsers
            },
            // Paginate the users
            paginatedUsers () {
                let newUsers = this.sortedUsers;

                // Sort users if there are any
                if (newUsers.length > 0) {
                    // Splice the results to showing size
                    const startIndex = this.resultsPerPage * (this.page - 1);
                    const endIndex = this.resultsPerPage * this.page;
                    newUsers = newUsers.slice(startIndex, endIndex)
                }

                return newUsers
            },
            // Number of results in users array
            totalCount () {
                return this.users.length
            }
        },
        methods: {
            search () {
                this.blurSearch();
                this.users = [];
                this.loading = true;
                this.page = 1;

                User.getUsersFake(this.searchTerm) // TODO: Change to proper api function
                    .then((res) => {
                        this.users = res.data;
                        this.loading = false;
                    })
                    .catch((err) => {
                        this.error = err;
                        this.loading = false;
                    })
            },
            blurSearch () {
                document.getElementById('search').blur()
            },
            orderResults (col) {
                // Remove the ordering if the column is clicked and the arrow is down
                if (this.orderCol === col && this.orderDirection) {
                    this.orderCol = null;
                    this.orderDirection = false;
                    return
                }

                // Updated order direction if the new column is the same as what is currently clicked
                this.orderDirection = this.orderCol === col;
                this.orderCol = col;
            },
            // Function for sorting a list by orderCol alphabetically
            sortAlpha (a, b) {
                if(a[this.orderCol] === null) { return -1 }
                if(b[this.orderCol] === null) { return 1 }
                if(a[this.orderCol] < b[[this.orderCol]]) { return 1; }
                if(a[this.orderCol] > b[[this.orderCol]]) { return -1; }
                return 0;
            },
            formattedAddress (address) {
                return address.slice(address.indexOf(',') + 2)
            },
            viewUser(id) {
                this.$router.push({name: 'viewUser', params: {userId: id}})
            },
            nameAndNickname (user) {
                if (user.nickname) {
                    return `${user.firstName} (${user.nickname})`
                }
                return user.firstName
            }
        }
    }
</script>

<style scoped>
.col-centered {
    margin: 0 auto;
    float: none;
}

.pointer {
    cursor: pointer;
}

</style>
