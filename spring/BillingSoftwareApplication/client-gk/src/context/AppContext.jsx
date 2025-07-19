import { createContext , useEffect, useState} from 'react';
import { fetchCategories } from '../service/CategoryService.js';    
import { fetchItems } from '../service/ItemService.js';

export const AppContext = createContext (null); 

export const AppContextProvider = (props) => {

    const [categories, setCategories] = useState([]);
    const [itemsData, setItemsData] = useState([]);
    const [auth,setAuth] = useState({token:null, role:null});
    const [cartItems,setCartItems] = useState([]);

    const clearCart = () => {
        setCartItems([]);
    }

    const addToCart = (item) => {
        const existingItem = cartItems.find(cartItem => cartItem.name === item.name);
        if(existingItem) {
            setCartItems(cartItems.map(cartItem => cartItem.name === item.name ? {...cartItem, quantity: cartItem.quantity + 1} : cartItem));
        } else {
            setCartItems([...cartItems,{...item,quantity: 1}]);
        }
    }

    const removeFromCart = (itemId) => {
        setCartItems(cartItems.filter(item => item.itemId !== itemId));
    }

    const updateQuantity = (itemId, newQuantity) => {
        setCartItems(cartItems.map(item => item.itemId === itemId ? {...item,quantity: newQuantity} : item));
    }

    useEffect(() => {
        // This is where we can fetch data or perform any side effects
        // For example, fetching categories, items, etc.

        async function loadData() {
             if (localStorage.getItem("token") && localStorage.getItem("role")) {
                setAuthData(
                    localStorage.getItem("token"),
                    localStorage.getItem("role")
                );
            }
            try {
                const response = await fetchCategories();
                const itemResponse = await fetchItems();
                setCategories(response?.data || []);
                setItemsData(itemResponse?.data || []);
                } catch (error) {
                    console.error(error);
                }
            // Now we can reterive our categories from anywhere in the application

        }

        // Make an API call to fetch Categories and before we fetch it we need a state at the top
        
        
        loadData();

    }, []);

    const setAuthData = (token,role) => {
        setAuth({token,role});
    }


    const contextValue = {
        //This contextValue we can pass it accross all other components
        //Add all the data 
        categories,
        setCategories,
        auth,   
        setAuthData,
        itemsData,
        setItemsData,
        addToCart,
        cartItems,
        updateQuantity,
        removeFromCart,
        clearCart
    }

    // Return the JSX for the context provider
    // For all of these children we will pass the context value
    return <AppContext.Provider value={contextValue}>
        {props.children}
    </AppContext.Provider>
}
