import Menubar from "./components/Menubar/Menubar";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import Dashboard from "./pages/Dashboard/Dashboard";
import ManagedUsers from "./pages/ManagedUsers/ManagedUsers";
import ManagedCategory from "./pages/ManagedCategory/ManagedCategory";
import ManagedItems from "./pages/ManagedItems/ManagedItems";
import Explore from "./pages/Explore/Explore";
import Login from "./pages/Login/Login";
import OrderHistory from "./pages/OrderHistory/OrderHistory";
import { Toaster } from "react-hot-toast";
import { use, useContext } from "react";
import { AppContext } from "./context/AppContext";
import NotFound from "./pages/NotFound/NotFound.jsx";

const App = () => {
    const location = useLocation();
    const {auth} = useContext(AppContext);

 const LoginRoute = ({element}) => {
        if(auth.token) {
            return <Navigate to="/dashboard" replace />;
        }
        return element;
    }

    const ProtectedRoute = ({element, allowedRoles}) => {
        if (!auth.token) {
            return <Navigate to="/login" replace />;
        }

        if (allowedRoles && !allowedRoles.includes(auth.role)) {
            return <Navigate to="/dashboard" replace />;
        }

        return element;
    }

    return (
        <div>
            {location.pathname !== "/login" && location.pathname !== '/' && <Menubar />}
            <Toaster />
            <Routes>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/explore" element={<Explore />} />
                {/*Admin only routes*/}
                <Route path="/category" element={<ProtectedRoute element={<ManagedCategory />} allowedRoles={['ROLE_ADMIN']} />} />
                <Route path="/users" element={<ProtectedRoute element={<ManagedUsers />} allowedRoles={["ROLE_ADMIN"]} />} />
                <Route path="/items" element={<ProtectedRoute element={<ManagedItems />} allowedRoles={["ROLE_ADMIN"]} /> } />

                <Route path="/login" element={<LoginRoute element={<Login />} />} />
                <Route path="/orders" element={<OrderHistory />} />
                <Route path="/" element={<Login />} />
                <Route path="*" element={<NotFound />} />

            </Routes>
        </div>
    );
}

export default App;