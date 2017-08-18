import React from "react";
import ReactDOM from "react-dom";

import Greeter from "./components/greeter";


ReactDOM.render(
    <div>
        <Greeter message="Students"/>
        <Greeter message="Andre Iske"/>
        <Greeter />
    </div>,
    document.getElementById('root'));

