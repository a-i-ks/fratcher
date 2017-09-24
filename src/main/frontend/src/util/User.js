import axios from "axios";
import Cookies from "universal-cookie";

class User {
    constructor() {
        this.reset();
        this.profile = {
            name: "Unknown"
        };
        this.userType = "N/A";
        this.profilePic = null;
        const cookies = new Cookies();
        const auth = cookies.get('auth');
        if (auth) {
            this.setCookieCredentials(auth);
        }
    }

    setCookieCredentials(credentials) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${credentials.token}`;
        this.set(credentials.user);
    }

    set(data) {
        this.profile.name = data.profile.name;
        this.email = data.email;
        this.id = data.id;
        this.userType = data.userType;
        this.profilePic = data.profilePic;
    }

    reset() {
        this.name = "";
        this.email = undefined;
        this.id = -1;
        this.userType = "N/A";
        this.profilePic = undefined;
    }

    isAuthenticated() {
        return this.email && this.id != -1;
    }

    isNotAuthenticated() {
        return !this.isAuthenticated();
    }

    isAdmin() {
        return this.userType === "ADMIN"
    }

    isMod() {
        return this.userType === "MODERATOR"
    }
}

// Singleton pattern in ES6.
export default (new User);