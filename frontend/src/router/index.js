import Vue from 'vue'
import Router from './custom-router'

import Landing from '../components/landing-page/Landing'
import RegisterPage from "../components/user/RegisterPage";
import LoginPage from "../components/LoginPage";
import Home from "../components/Home";
import EditUserProfile from "../components/user/EditUserProfile";
import BusinessProfilePage from "../components/business/BusinessProfilePage";
import RegisterBusinessPage from "../components/business/RegisterBusinessPage";
import Catalogue from "../components/product-catalogue/ProductCatalogue";
import EditProductPage from "../components/product-catalogue/EditProductPage";
import InventoryPage from "../components/inventory/InventoryPage";
import SaleListings from "../components/sale-listing/SaleListings";
import EditInventoryItemPage from "../components/inventory/EditInventoryItemPage";
import Marketplace from "../components/marketplace/Marketplace";
import Search from "@/components/Search";
import EditBusiness from "@/components/business/EditBusiness";
import BrowseSaleListings from "@/components/sale-listing/BrowseSaleListings";
import PasswordReset from "@/components/user/PasswordReset";
import UserPurchasesPage from "@/components/user/UserPurchasesPage";
import ProfilePage from "@/components/user/ProfilePage";

const routes = [
    {
        path: '/',
        name: 'landing',
        component: Landing
    },
    {
        path: '/search',
        name: 'search',
        component: Search
    },
    {
        path: '/users/:userId/purchases',
        name: 'purchases',
        component: UserPurchasesPage
    },
    {
        path: '/users/:userId/edit',
        name: 'editUser',
        component: EditUserProfile
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
        path: '/home',
        name: 'home',
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
        path: '/businesses/:businessId/edit',
        name: 'editBusiness',
        component: EditBusiness
    },
    {
        path: '/businesses/:businessId/products',
        name: 'viewCatalogue',
        component: Catalogue
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
        path: '/businesses/:businessId/inventory/:inventoryItemId',
        name: 'editInventoryItem',
        component: EditInventoryItemPage
    },
    {
        path: '/businesses/:businessId/listings',
        name: 'listings',
        component: SaleListings
    },
    {
        path: '/marketplace',
        name: 'marketplace',
        component: Marketplace
    },
    {
        path: '/listings',
        name: 'browseListings',
        component: BrowseSaleListings
    },
    {
        path: '/reset-password/:token',
        name: 'resetPassword',
        component: PasswordReset
    }
];

const base = process.env.VUE_APP_BASE_URL || '/';

Vue.use(Router, {routes, base});
