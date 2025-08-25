document.addEventListener("DOMContentLoaded", function () {
    const countrySelect = document.getElementById("countrySelect");
    const stateSelect = document.getElementById("stateSelect");
    const citySelect = document.getElementById("citySelect");

    const currentCountry = countrySelect.getAttribute("data-current-country") || "";
    const currentState = stateSelect.getAttribute("data-current-state") || "";
    const currentCity = citySelect.getAttribute("data-current-city") || "";

    function resetSelect(sel, placeholder, disable = true) {
        sel.innerHTML = `<option value="">${placeholder}</option>`;
        sel.disabled = !!disable;
    }

    function addOption(sel, value, label) {
        const opt = document.createElement("option");
        opt.value = value;
        opt.textContent = label ?? value;
        sel.appendChild(opt);
    }

    async function loadCountries() {
        try {
            resetSelect(countrySelect, "Select Country", false);
            const res = await fetch("https://countriesnow.space/api/v0.1/countries/positions");
            const json = await res.json();
            (json.data || []).forEach(c => addOption(countrySelect, c.name, c.name));

            if (currentCountry) {
                countrySelect.value = currentCountry;
                await loadStates(currentCountry, true); // true => vine din preselect
            }
        } catch (e) {
            console.error("Error loading countries:", e);
        }
    }

    async function loadStates(country, isPrefill = false) {
        resetSelect(stateSelect, "Select County");
        resetSelect(citySelect, "Select City");

        if (!country) return;

        try {
            stateSelect.disabled = true;
            addOption(stateSelect, "", "Loading...", "Loading...");
            const res = await fetch("https://countriesnow.space/api/v0.1/countries/states", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({country})
            });
            const json = await res.json();

            resetSelect(stateSelect, "Select County", false);

            const states = (json.data && json.data.states) ? json.data.states : (json.data || []);
            states.forEach(s => addOption(stateSelect, s.name || s, s.name || s));

            if (isPrefill && currentState) {
                stateSelect.value = currentState;
                await loadCities(country, currentState, true);
            }
        } catch (e) {
            console.error("Error loading states:", e);
            resetSelect(stateSelect, "Select County");
        }
    }

    async function loadCities(country, state, isPrefill = false) {
        resetSelect(citySelect, "Select City");

        if (!country || !state) return;

        try {
            citySelect.disabled = true;
            addOption(citySelect, "", "Loading...");
            const res = await fetch("https://countriesnow.space/api/v0.1/countries/state/cities", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({country, state})
            });
            const json = await res.json();

            resetSelect(citySelect, "Select City", false);

            (json.data || []).forEach(city => addOption(citySelect, city, city));

            if (isPrefill && currentCity) {
                citySelect.value = currentCity;
            }
        } catch (e) {
            console.error("Error loading cities:", e);
            resetSelect(citySelect, "Select City");
        }
    }

    countrySelect.addEventListener("change", () => {
        loadStates(countrySelect.value, false);
    });

    stateSelect.addEventListener("change", () => {
        loadCities(countrySelect.value, stateSelect.value, false);
    });

    loadCountries();
});
