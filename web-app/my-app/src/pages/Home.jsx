import React, {Component} from 'react';
import './Home.scss'
import logo from "./refinitiv-logo-schema.png"

class Home extends Component {
    constructor(props) {
        super(props);
        this.state = {
            tasks: []
        }
    }

    componentDidMount() {
        let self = this;
        fetch('http://localhost:4000/tasks', {
            method: 'GET'
        }).then(function(response) {
            if (response.status >= 400) {
                throw new Error("Bad response from server");
            }
            return response.json();
        }).then(function(data) {
            self.setState({tasks: data});
        }).catch(err => {
            console.log('caught it!',err);
        })
    }

    render() {
        return (
            <div className="task">
                <nav className="navbar navbar-expand-lg navbar-light bg-light">
                    <a className="navbar-brand" href="#">
                        <img className="refLogo"  src={logo} height="50" alt={"REFINITIV"}/>
                    </a>
                </nav>
                <div className="container">
                    <div className="panel panel-default p50 uth-panel">
                        <table className="table table-hover">
                            <thead>
                            <tr>
                                <th>Task name</th>
                                <th>Task description</th>
                                <th>Task Creation Time</th>
                                <th>URL</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            {this.state.tasks.map(taskCB =>
                                <tr key={taskCB.id}>
                                    <td>{taskCB.taskName} </td>
                                    <td>{taskCB.taskDescription}</td>
                                    <td>{taskCB.taskCreationTime}</td>
                                    <td>{taskCB.taskURL}</td>
                                    <td><a>Edit</a>|<a>Delete</a></td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        );
    }
}

export default Home;