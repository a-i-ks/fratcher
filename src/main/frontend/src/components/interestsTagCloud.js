import React from "react";
import {TagCloud} from "react-tagcloud";

class InterestsTagCloud extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        console.log("render tag cloud");
        let keywords;
        if (this.props.data) {
            keywords = this.props.data.map(function (keyword) {
                return {value: keyword.value, count: keyword.count, key: keyword.id};
            });
            console.log(keywords);
        }
        return (
            <div>
                {keywords ?
                    <TagCloud minSize={12}
                              maxSize={35}
                              tags={keywords}/> :
                    <div/>}
            </div>)
    }
}

export default InterestsTagCloud;
