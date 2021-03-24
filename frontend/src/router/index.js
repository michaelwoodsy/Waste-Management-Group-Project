import Vue from 'vue'
import Router from './custom-router'

import Home from '../components/Home'
import UserSearch from '../components/UserSearch'
import Register_page from "../components/Register_page";
import Login_page from "../components/Login_page";
import Profile_page from "../components/Profile_page";
import Public_profile_page from "../components/Public_profile_page";

const routes = [
    {
        path: '/',
        name: 'home',
        component: Home
    },
    {
        path: '/users',
        name: 'users',
        component: UserSearch
    },
    {
        path: '/users/:userId',
        name: 'viewUser',
        component: Public_profile_page
    },
    {
        path: '/register',
        name: 'register',
        component: Register_page
    },
    {
        path: '/login',
        name: 'login',
        component: Login_page
    },
    {
        path: '/profile',
        name: 'user',
        component: Profile_page
    }
];


Vue.use(Router, {routes});
