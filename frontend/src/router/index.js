import Vue from 'vue'
import Router from './custom-router'

import Landing from '../components/Landing'
import UserSearch from '../components/UserSearch'
import RegisterPage from "../components/RegisterPage";
import LoginPage from "../components/LoginPage";
import Home from "../components/Home";
import ProfilePage from "../components/ProfilePage";
import BusinessProfilePage from "@/components/BusinessProfilePage";
import RegisterBusinessPage from "@/components/RegisterBusinessPage";

const routes = [
    {
        path: '/',
        name: 'landing',
        component: Landing
    },
    {
        path: '/users/search',
        name: 'users',
        component: UserSearch
    },
    {
        path: '/users/:userId',
        name: 'viewUser',
        component: ProfilePage
    },
    {
        path: '/register',
        name: 'register',
        component: RegisterPage
    },
    {
        path: '/login',
        name: 'login',
        component: LoginPage
    },
    {
        path: '/home', //TODO: change to '/' after user logged in
        name: 'user',
        component: Home
    },
    {
        path: '/businesses/:businessId',
        name: 'viewBusiness',
        component: BusinessProfilePage
    },
    {
        path: '/registerbusiness',
        name: 'registerBusiness',
        component: RegisterBusinessPage
    }
];

const base = process.env.VUE_APP_BASE_URL || '/';

Vue.use(Router, {routes, base});
