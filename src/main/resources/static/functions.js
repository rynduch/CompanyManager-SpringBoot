function deleteEmployee(id) {
    fetch(`/api/employee/${id}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                alert('Employee deleted.');
            } else {
                alert('Employee does not exist.');
            }
        })
        .catch(error => {
            console.error(error);
            alert('Query execution failed.');
        });
}

function deleteGroup(id) {
    fetch(`/api/group/${id}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                alert('Group deleted.');
            } else {
                alert('Group does not exist.');
            }
        })
        .catch(error => {
            console.error(error);
            alert('Query execution failed.');
        });
}

function removeEmployeeFromGroup(gr_id, emp_id) {
    fetch(`/api/employee_from_group/${gr_id}/${emp_id}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                alert('Employee removed from group.');
            } else {
                alert('Employee/Group does not exist.');
            }
        })
        .catch(error => {
            console.error(error);
            alert('Query execution failed.');
        });
}

function deleteRate(id) {
    fetch(`/api/rate/${id}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                alert('Rate deleted.');
            } else {
                alert('Rate does not exist.');
            }
        })
        .catch(error => {
            console.error(error);
            alert('Query execution failed.');
        });
}

async function loadIntoTable(url, table) {
    const tableBody = table.querySelector("tbody");
    const response = await fetch(url);
    const data = await response.json();
    tableBody.innerHTML = "";
    for (const row of data) {
        const rowElement = document.createElement("tr");
        for (const key in row) {
            const cellElement = document.createElement("td");
            const value = row[key];
            if (key === 'employees') {
                if (Array.isArray(value)) {
                    const employeesNames = [];
                    for (const employee of value) {
                        if (typeof employee === 'object') {
                            employeesNames.push(`${employee.name} ${employee.lastname}`);
                        } else {
                            const foundEmployee = data.find(dataItem => dataItem.employees.some(emp => emp.employee_id === employee)).employees.find(emp => emp.employee_id === employee);
                            if (foundEmployee) {
                                employeesNames.push(`${foundEmployee.name} ${foundEmployee.lastname}`);
                            }
                        }
                    }
                    cellElement.textContent = employeesNames.join(", ");
                }
            } else if (key === 'rates') {
                const ratesValues = [];
                for (const rate of value) {
                    ratesValues.push(rate.value);
                }
                cellElement.textContent = ratesValues.join(", ");
            } else if (key === 'group') {
                if (typeof value === 'object') {
                    cellElement.textContent = value.name;
                } else {
                    const foundGroup = data.find(dataItem => dataItem.group.group_id === value);
                    if (foundGroup) {
                        cellElement.textContent = foundGroup.group.name;
                    } else {
                        cellElement.textContent = "Unknown";
                    }
                }
            } else if (key === 'percentage') {
                cellElement.textContent = String(Math.round(parseFloat(value) * 100));
            } else {
                cellElement.textContent = value;
            }
            rowElement.appendChild(cellElement);
        }
        tableBody.appendChild(rowElement);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    let path = window.location.pathname;
    if (path === '/api/employee_table') {
        await loadIntoTable("/api/employee_list", document.querySelector("#employee_table"));
    } else if (path === '/api/group_table') {
        await loadIntoTable("/api/group_list", document.querySelector("#group_table"));
    } else if (path === '/api/rate_table') {
        await loadIntoTable("/api/rate_list", document.querySelector("#rate_table"));
    }
});
