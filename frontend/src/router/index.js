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
import Catalogue from "@/components/ProductCatalogue";
import EditProductPage from "@/components/EditProductPage";
import InventoryPage from "@/components/InventoryPage";
import CreateProductPage from "@/components/CreateProductPage";
import SaleListings from "@/components/SaleListings";
import EditInventoryItemPage from "@/components/EditInventoryItemPage";
import CreateInventoryItemPage from "@/components/CreateInventoryItemPage";

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
        path: '/home', //TODO: change to '/' after user logged in?
        name: 'user',
        component: Home
    },
    {
        path: '/businesses/:businessId',
        name: 'viewBusiness',
        component: BusinessProfilePage
    },
    {
        path: '/businesses',
        name: 'registerBusiness',
        component: RegisterBusinessPage
    },
    {
        path: '/businesses/:businessId/products',
        name: 'viewCatalogue',
        component: Catalogue
    },
    {
        path: '/businesses/:businessId/products/create',
        name: 'createProduct',
        component: CreateProductPage
    },
    {
        path: '/businesses/:businessId/products/:productId',
        name: 'editProduct',
        component: EditProductPage
    },
    {
        path: '/businesses/:businessId/inventory',
        name: 'InventoryPage',
        component: InventoryPage
    },
    {
        path: '/businesses/:businessId/inventory/create',
        name: 'CreateInventoryItem',
        component: CreateInventoryItemPage
    },
    {
        path: '/businesses/:businessId/inventory/:inventoryItemId',
        name: 'editInventoryItem',
        component: EditInventoryItemPage
    },
    {
        path: '/businesses/:businessId/listings',
        name: 'listings',
        component: SaleListings
    }
];

const base = process.env.VUE_APP_BASE_URL || '/';

Vue.use(Router, {routes, base});
