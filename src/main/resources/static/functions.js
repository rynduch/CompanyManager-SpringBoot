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
async function loadIntoTable(url, table){
    const tableBody = table.querySelector("tbody");
    const response = await fetch(url);
    const data = await response.json();
    tableBody.innerHTML = "";
    for (const row of data){
        const rowElement = document.createElement("tr");
        for (const key in row){
            const cellElement = document.createElement("td");
            const value = row[key];
            if (key === 'employees') {
                if (Array.isArray(value)) {
                    const employeesNames = [];
                    for (const employee of value) {
                        if (typeof employee === 'object') {
                            employeesNames.push(`${employee.name} ${employee.lastname}`);
                        } else {
                            const foundEmployee = data.find(list => list.employees.some(emp => emp.employee_id === employee)).employees.find(emp => emp.employee_id === employee);
                            if (foundEmployee) {
                                employeesNames.push(`${foundEmployee.name} ${foundEmployee.lastname}`);
                            }
                        }
                    }
                    cellElement.textContent = employeesNames.join(", ");
                }
            } else if (key === 'rates'){
                const ratesValues = [];
                for (const rate of value) {
                        ratesValues.push(rate.value);
                }
                cellElement.textContent = ratesValues.join(", ");
            } else if (key === 'group'){
                if (typeof value === 'object') {
                    cellElement.textContent = value.name; // Jeśli tak, zwracamy nazwę grupy
                } else {
                    const foundGroup = data.find(item => item.group.group_id === value);
                    if (foundGroup) {
                        cellElement.textContent = foundGroup.group.name;
                    } else {
                        cellElement.textContent = "Unknown";
                    }
                }
            }
            else {
                cellElement.textContent = value;
            }
            rowElement.appendChild(cellElement);
        }
        tableBody.appendChild(rowElement);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    let path = window.location.pathname;
    if(path === '/api/employee_table') {
        await loadIntoTable("/api/employee_list", document.querySelector("#employee_table"));
    } else if(path === '/api/group_table') {
        await loadIntoTable("/api/group_list", document.querySelector("#group_table"));
    } else if(path === '/api/rate_table') {
        await loadIntoTable("/api/rate_list", document.querySelector("#rate_table"));
    }
});
