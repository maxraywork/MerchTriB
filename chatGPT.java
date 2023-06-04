
    public static class Task {
        public String name;
        public String address;
        public String id;
        public String addressLink;
        public ArrayList<String> images;

        public Task() {};

        public Task(String id, String name, String address, String addressLink, ArrayList<String> images) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.addressLink = addressLink;
            this.images = images;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAddressLink() {
            return addressLink;
        }

        public void setAddressLink(String addressLink) {
            this.addressLink = addressLink;
        }
    }

    public static class CheckGeoActivity extends AppCompatActivity {


        private static int SPLASH_TIME_OUT = 500;
        Timer t = new Timer();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_check_geo);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle(R.string.wait);
                setSupportActionBar(toolbar);
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(false);


            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t.cancel();

                            Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
                            startActivity(intent);
                            MainActivity.BackActivity.finish();
                            finish();

                        }
                    });
                }
            }, SPLASH_TIME_OUT, SPLASH_TIME_OUT);

        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            super.onBackPressed();
            t.cancel();
            return true;
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            t.cancel();
        }
    }

    public static class MainFragment extends Fragment {

        public ArrayList<Task> TASKS = new ArrayList<>();
        private DatabaseReference mDatabase;
        SharedPreferences sharedPreferences;
        String userEmail, companyName, companyNameOriginal;
        boolean isAdmin;
        private FirebaseUser user;
        RecyclerView lv;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_main, container, false);

            //        user = FirebaseAuth.getInstance().getCurrentUser();
            sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
            userEmail = sharedPreferences.getString("userEmailShort", "");
            companyName = sharedPreferences.getString("companyName", "");
            isAdmin = sharedPreferences.getBoolean("isAdmin", false);
            companyNameOriginal = sharedPreferences.getString("companyNameOriginal", null);

            lv = v.findViewById(R.id.list_of_tasks);
            LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lv.getContext(),
                    llm.getOrientation());
            lv.addItemDecoration(dividerItemDecoration);
            lv.setLayoutManager(llm);

            TextView emptyText = v.findViewById(R.id.emptyText);

            mDatabase = FirebaseDatabase.getInstance().getReference();

            ProgressBar progressBar = v.findViewById(R.id.progress_circular);
            mDatabase.child("companies/" + companyName + "/tasks/current").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TASKS = new ArrayList<>();
                    // Get Post object and use the values to update the UI
                    for (DataSnapshot dataTask: dataSnapshot.getChildren()) {
                        TASKS.add(dataTask.getValue(Task.class));

                    }
                    lv.setAdapter(new TasksAdapter(getContext() , TASKS, "current", isAdmin, companyName));
                    progressBar.setVisibility(View.INVISIBLE);
                    if (!TASKS.isEmpty()) {
                        emptyText.setVisibility(View.INVISIBLE);
                    } else {
                        emptyText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            lv.setAdapter(new TasksAdapter(getContext() , TASKS, "current",  isAdmin, companyName));



            SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd.MM", Locale.getDefault());
            String date = formatter.format(new Date());
            Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
            setHasOptionsMenu(true);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (toolbar != null) {
                if (isAdmin && companyNameOriginal != null) {
                    toolbar.setTitle(companyNameOriginal);
                } else {
                    toolbar.setTitle(date);
                }
                if (activity != null) {
                    activity.setSupportActionBar(toolbar);
                }
            }

            return v;

        }


        @Override
        public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.menu_main, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        // Заглушка, работа с меню
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.menu_main_add) {
                startActivity(new Intent(this.getActivity(), AddTaskActivity.class));
            } else if (id == R.id.menu_main_logOut) {
                FirebaseAuth.getInstance().signOut();
                sharedPreferences.getAll().clear();
                startActivity(new Intent(getActivity(), MainActivity.class));
                Objects.requireNonNull(getActivity()).finish();
            }

            return super.onOptionsItemSelected(item);
        }




        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
        }
    }

    public static class ErrorActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_error);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle(getEmojiByUnicode(0x1F62D));
                setSupportActionBar(toolbar);
            }

            getWindow().setStatusBarColor(getResources().getColor(R.color.Error, this.getTheme()));
        }

        public String getEmojiByUnicode(int cry_smile){
            return new String(Character.toChars(cry_smile));
        }

        public void repeat_error(View view) {
            Intent intent = new Intent(ErrorActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

    public static class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

        private Context ctx;
        private ArrayList<String> mData;
        private ImageButton delete;
        private TextView name;
        private String companyName;

        public UsersAdapter(Context ctx, ArrayList<String> data, String companyName) {
            this.ctx = ctx;
            this.mData = data;
            this.companyName = companyName;

        }


        @NonNull
        @Override
        public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);

            return new UsersViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull UsersAdapter.UsersViewHolder holder, int position) {
            String current = mData.get(position);
            name.setText(current);
            delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                AlertDialog dialog = builder.setTitle("Удаление")
                        .setMessage("Вы уверены что хотите удалить этого пользователя?")
                        .setPositiveButton("Да", (dialogg, id) -> {
                            // Закрываем окно
                            String email = current.replace("@", "").replace(".", "").toLowerCase();
                            FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
                            mDataBase.getReference("companies/" + companyName + "/users/" + email).removeValue();
                            mDataBase.getReference("users/" + email).removeValue();
                            dialogg.cancel();
                        }).setNegativeButton("Отмена", (dialogg, id) -> {
                            dialogg.cancel();
                        }).create();
                dialog.setOnShowListener(arg0 -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ctx, R.color.primary));
                });
                builder.show();
            });
        }


        @Override
        public int getItemCount() {
            return mData.size();
        }


        public class UsersViewHolder extends RecyclerView.ViewHolder {
            public UsersViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.list_item_name);
                delete = itemView.findViewById(R.id.delete);

            }

        }
    }

    public static class User {

        public String company;
        public boolean admin;

        public User() {};

        public User(String company, boolean admin) {
            this.company = company;
            this.admin = admin;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public boolean isAdmin() {
            return admin;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }
    }

    public static class Company {

        public String name;
        public ArrayList<CompanyUser> users;
        public ArrayList<TaskList> tasks;

        public Company() {};


        public Company(String name, ArrayList<CompanyUser> users, ArrayList<TaskList> tasks) {
            this.name = name;
            this.users = users;
            this.tasks = tasks;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<CompanyUser> getUsers() {
            return users;
        }

        public void setUsers(ArrayList<CompanyUser> users) {
            this.users = users;
        }

        public ArrayList<TaskList> getTasks() {
            return tasks;
        }

        public void setTasks(ArrayList<TaskList> tasks) {
            this.tasks = tasks;
        }
    }

    public static class SendTodayFragment extends Fragment {

        public ArrayList<Task> TASKS = new ArrayList<Task>();
        private DatabaseReference mDatabase;
        SharedPreferences sharedPreferences;
        String userEmail, companyName, companyNameOriginal;
        boolean isAdmin;
        RecyclerView lv;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_send_today, container, false);

            lv = v.findViewById(R.id.list_of_tasks);
            LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lv.getContext(),
                    llm.getOrientation());
            lv.addItemDecoration(dividerItemDecoration);
            lv.setLayoutManager(llm);
            TextView emptyText = v.findViewById(R.id.emptyText);

            sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
            userEmail = sharedPreferences.getString("userEmailShort", "");
            companyName = sharedPreferences.getString("companyName", "");
            isAdmin = sharedPreferences.getBoolean("isAdmin", false);
            companyNameOriginal = sharedPreferences.getString("companyNameOriginal", null);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            ProgressBar progressBar = v.findViewById(R.id.progress_circular);
            mDatabase.child("companies/" + companyName + "/tasks/done").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TASKS = new ArrayList<>();
                    // Get Post object and use the values to update the UI
                    for (DataSnapshot dataTask: dataSnapshot.getChildren()) {
                        TASKS.add(dataTask.getValue(Task.class));
                    }
                    lv.setAdapter(new TasksAdapter(getContext() , TASKS, "done", isAdmin, companyName));
                    progressBar.setVisibility(View.INVISIBLE);
                    if (!TASKS.isEmpty()) {
                        emptyText.setVisibility(View.INVISIBLE);
                    } else {
                        emptyText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            lv.setAdapter(new TasksAdapter(getContext() , TASKS, "done", isAdmin, companyName));


            Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (toolbar != null) {
                toolbar.setTitle("История");
                if (activity != null) {
                    activity.setSupportActionBar(toolbar);
                }
            }

            return v;

        }



        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
        }
    }

    public static class AdminActivity extends AppCompatActivity {

        public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        MainFragment mainFragment = new MainFragment();
        SendTodayFragment oldFragment = new SendTodayFragment();
        UsersFragment usersFragment = new UsersFragment();


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin);

            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container_main) == null) {
                // start new Activity
                loadFragment(mainFragment);
            }
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.inflateMenu(R.menu.admin_bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.tasks);
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.tasks:
                        loadFragment(mainFragment);
                        return true;
                    case R.id.users:
                        loadFragment(usersFragment);
                        return true;
                    case R.id.tasks_old:
                        loadFragment(oldFragment);
                        return true;
                }
                return false;
            });


        }


        public void loadFragment(Fragment fragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container_main, fragment);
            ft.commit();
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }


        @Override
        public void onBackPressed() {
            super.onBackPressed();
        }

    }

    public static class RegisterCompanyActivity extends AppCompatActivity {
        EditText name;
        Button submit;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register_company);

            name = findViewById(R.id.company);
            submit = findViewById(R.id.submit);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle("Регистрация компании");
                setSupportActionBar(toolbar);

            }

            //Зарегистрировать компанию
            submit.setOnClickListener(v -> {
                submit.setClickable(false);
                String data = name.getText().toString();
                if (!data.isEmpty() && data.length() >= 2) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace("@", "").replace(".", "").toLowerCase();
                    FirebaseDatabase mBase = FirebaseDatabase.getInstance();
                    HashMap user = new HashMap();
                    String result = transliterate(data);
                    String companyName = result + System.currentTimeMillis();
                    user.put("company", companyName);

                    mBase.getReference("users").child(email).updateChildren(user).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mBase.getReference("companies").child(companyName).setValue(new Company(data, null, null)).addOnCompleteListener(t -> {
                                finish();
                            });
                            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + task.getException(), Toast.LENGTH_LONG).show();
                            submit.setClickable(true);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Название должно быть больше двух букв", Toast.LENGTH_LONG).show();
                    submit.setClickable(true);
                }
            });
        }


        public static String transliterate(String message){
            char[] abcCyr =   {' ','а','б','в','г','д','е','ё', 'ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х', 'ц','ч', 'ш','щ','ъ','ы','ь','э', 'ю','я','А','Б','В','Г','Д','Е','Ё', 'Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х', 'Ц', 'Ч','Ш', 'Щ','Ъ','Ы','Ь','Э','Ю','Я','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
            String[] abcLat = {"","a","b","v","g","d","e","e","zh","z","i","y","k","l","m","n","o","p","r","s","t","u","f","h","ts","ch","sh","sch", "","i", "","e","ju","ja","A","B","V","G","D","E","E","Zh","Z","I","Y","K","L","M","N","O","P","R","S","T","U","F","H","Ts","Ch","Sh","Sch", "","I", "","E","Ju","Ja","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < message.length(); i++) {
                for (int x = 0; x < abcCyr.length; x++ ) {
                    if (message.charAt(i) == abcCyr[x]) {
                        builder.append(abcLat[x]);
                    }
                }
            }
            return builder.toString();
        }
    }

    public static class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

        private Context ctx;
        private ArrayList<Task> mArrayTask;
        private ImageButton imageButton;
        private TextView name;
        private TextView address;
        private LinearLayout parentLayout;
        private String type;
        private boolean isAdmin;
        private String companyName;

        public TasksAdapter(Context ctx, ArrayList<Task> task, String type, boolean isAdmin, String companyName) {
            this.ctx = ctx;
            this.mArrayTask = task;
            this.type = type;
            this.isAdmin = isAdmin;
            this.companyName = companyName;

        }


        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_task_list_item, parent, false);

            return new TaskViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TasksAdapter.TaskViewHolder holder, int position) {
            Task current = mArrayTask.get(position);
            name.setText(current.getName());
            address.setText(current.getAddress());
            if (current.getAddressLink() != null) {
                imageButton.setOnClickListener(view -> {
                    Uri uri = Uri.parse(current.getAddressLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    ctx.startActivity(intent);
                });

            } else {
                imageButton.setVisibility(View.INVISIBLE);
            }

            parentLayout.setOnClickListener(v -> {
                Intent intent = new Intent(ctx, TaskActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("id", current.id);
                intent.putExtra("name", current.name);
                intent.putExtra("address", current.address);
                intent.putExtra("addressLink", current.addressLink);
                ctx.startActivity(intent);
            });
            if (isAdmin) {
                parentLayout.setOnLongClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Удаление")
                            .setMessage("Вы уверены что хотите удалить это задание?")
                            .setPositiveButton("Да", (dialog, id) -> {
                                // Закрываем окно
                                FirebaseDatabase.getInstance().getReference("companies/" + companyName + "/tasks/" + type + "/" + current.id).removeValue();
                                FirebaseStorage.getInstance().getReference("uploads/" + current.id).delete();
                                dialog.cancel();
                            }).setNegativeButton("Отмена", (dialog, id) -> {
                                dialog.cancel();
                            });
                    builder.create().show();
                    return false;
                });
            }
        }


        @Override
        public int getItemCount() {
            return mArrayTask.size();
        }


        public class TaskViewHolder extends RecyclerView.ViewHolder {
            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                imageButton = itemView.findViewById(R.id.task_item_button);
                name = itemView.findViewById(R.id.list_item_name);
                address = itemView.findViewById(R.id.list_item_link);
                parentLayout = itemView.findViewById(R.id.parent);

            }

        }
    }

    public static class CompanyUser {
        public String email;

        public CompanyUser(){};
        public CompanyUser(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class MainActivity extends AppCompatActivity {

        public static Activity BackActivity;

        Fragment frag_main = new MainFragment();
        Fragment frag_send_today = new SendTodayFragment();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                // Проверить пользователя
                FirebaseDatabase.getInstance().getReference("users").child(user.getEmail().replace("@", "").replace(".", "").toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(User.class) != null) {

                            User userData = dataSnapshot.getValue(User.class);
                            boolean isCompany = userData.company != null;
                            if (isCompany) {
                                SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                                preferences.edit().putString("companyName", userData.company).putString("userEmailShort", user.getEmail().replace("@", "").replace(".", "").toLowerCase()).putBoolean("isAdmin", userData.admin).apply();
                                FirebaseDatabase.getInstance().getReference("companies/" + userData.company + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(String.class) != null) {
                                            preferences.edit().putString("companyNameOriginal", dataSnapshot.getValue(String.class)).apply();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            if (userData.admin) {
                                if (isCompany) {
                                    startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                                } else {
                                    startActivity(new Intent(getApplicationContext(), RegisterCompanyActivity.class));
                                }
                                finish();
                            } else {
                                updateUI();
                            }
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }


        }

        private void updateUI() {
            findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
            BackActivity = this;
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container_main) == null) {
                // start new Activity
                loadFragment(frag_main);
            }
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.main);
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.send_today:
                        loadFragment(frag_send_today);
                        return true;
                    case R.id.main:
                        loadFragment(frag_main);
                        return true;
                }
                return false;
            });
        }


        public void loadFragment(Fragment fragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container_main, fragment);
            ft.commit();
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }


        @Override
        public void onBackPressed() {
            super.onBackPressed();
        }


    }

    public static class UsersFragment extends Fragment {

        DatabaseReference mDatabase;
        RecyclerView recyclerView;
        String companyName;
        TextView emptyText;
        ProgressBar progressBar;
        ArrayList<String> users = new ArrayList<>();
        Button addButton;
        EditText email;
        FirebaseDatabase mAddData;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_users, container, false);
            companyName = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).getString("companyName", "");
            progressBar = v.findViewById(R.id.progress_circular);
            mAddData = FirebaseDatabase.getInstance();
            mDatabase = mAddData.getReference("companies/" + companyName + "/users");
            emptyText = v.findViewById(R.id.emptyText);
            addButton = v.findViewById(R.id.addButton);
            email = v.findViewById(R.id.email);


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.INVISIBLE);
                    users = new ArrayList<>();
                    if (dataSnapshot.getValue() != null) {

                        progressBar.setVisibility(View.INVISIBLE);
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            users.add(item.getValue(String.class));
                        }

                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                    }
                    recyclerView.setAdapter(new UsersAdapter(getContext(), users, companyName));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Что-то пошло не так: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            recyclerView = v.findViewById(R.id.list_of_tasks);
            LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    llm.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setLayoutManager(llm);

            Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (toolbar != null) {
                toolbar.setTitle("Работники");
                if (activity != null) {
                    activity.setSupportActionBar(toolbar);
                }
            }

            addButton.setOnClickListener(view -> {
                if (!email.getText().toString().isEmpty() && isValid(email.getText().toString())) {
                    String originalEmail = email.getText().toString();
                    String emailShort = originalEmail.replace("@","").replace(".","").toLowerCase();
                    mAddData.getReference("users").child(emailShort).setValue(new User(companyName, false));
                    mAddData.getReference("companies").child(companyName).child("users").child(emailShort).setValue(originalEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                email.setText("");
                            } else {
                                Toast.makeText(getContext(), "Что-то пошло не так: " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Поле пустое или введена не электронная почта", Toast.LENGTH_LONG).show();
                }
            });


            return v;
        }

        public static boolean isValid(String email) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            Pattern pat = Pattern.compile(emailRegex);
            if (email == null)
                return false;
            return pat.matcher(email).matches();
        }
    }

    public static class TaskList {
        public ArrayList<Task> array;

        public TaskList(){};

        public TaskList(ArrayList<Task> array) {
            this.array = array;
        }

        public ArrayList<Task> getArray() {
            return array;
        }

        public void setArray(ArrayList<Task> array) {
            this.array = array;
        }
    }

    public static class TaskActivity extends AppCompatActivity implements GalleryAdapter.OnImageListener {

        private RecyclerView gvGallery;
        private GalleryAdapter galleryAdapter;
        private FloatingActionButton addPhotoButton;

        public ArrayList<Uri> mArrayUri = new ArrayList<>();
        public ArrayList<String> mArrayUriDone = new ArrayList<>();
        public ArrayList<Uri> mArrayUriAbsolute = new ArrayList<>();
        public ArrayList<Uri> mArrayLocal = new ArrayList<>();
        int PICK_IMAGE_MULTIPLE = 1;

        private StorageReference mStorageRef;
        private DatabaseReference mDatabaseRef;

        private ProgressBar progressBar;

        GalleryAdapter.OnImageListener context;
        Context origContext;

        String title = "Ошибка",
                idTask = "",
                type = "current",
                companyName, userEmail;
        TextView emptyText;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_task);
            context = this;
            origContext = this;

            emptyText = findViewById(R.id.emptyText);
            Intent intent = getIntent();
            title = intent.getStringExtra("name");
            idTask = intent.getStringExtra("id");
            type = intent.getStringExtra("type") == null ? "current" : intent.getStringExtra("type");
            SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            companyName = preferences.getString("companyName", "");
            userEmail = preferences.getString("userEmailShort", "");

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle(title);
                setSupportActionBar(toolbar);
            }

            mStorageRef = FirebaseStorage.getInstance().getReference("uploads/" + idTask);
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("companies/" + companyName + "/tasks");

            progressBar = findViewById(R.id.progress_circular);
            gvGallery = findViewById(R.id.rev_photo);
            gvGallery.setLayoutManager(new GridLayoutManager(this, 3));
            addPhotoButton = findViewById(R.id.floatingActionButton);

            mDatabaseRef.child(type).child(idTask).child("images").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mArrayUriAbsolute = new ArrayList<>();
                    // Get Post object and use the values to update the UI
                    for (DataSnapshot dataUri : dataSnapshot.getChildren()) {
                        mArrayUriAbsolute.add(Uri.parse(dataUri.getValue(String.class)));
                    }
                    mArrayUri.clear();
                    mArrayUri.addAll(mArrayUriAbsolute);
                    mArrayUri.addAll(mArrayLocal);
                    gvGallery.setAdapter(new GalleryAdapter(origContext, mArrayUri, context));
                    progressBar.setVisibility(View.INVISIBLE);
                    if (mArrayUri.isEmpty()) {
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        emptyText.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            addPhotoButton.setOnClickListener(view -> requestPermissions(0));

        }

        private void requestPermissions(int i) {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    if (i == 0) {
                        selectImagesFromGallery();
                    } else if (i == 1) {
                        addImagesInRecycler(mArrayUri);
                    }

                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    Toast.makeText(TaskActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }


            };
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("Если вы не дадите разрешения, то не сможете пользоваться этим приложением\n\nПожалуйста, дайте разрешения в [Настройки] > [Приложения]")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .check();
        }

        private void selectImagesFromGallery() {
            try {

                // initialising intent
                Intent intent = new Intent();

                // setting type to select to be image
                intent.setType("image/*");

                // allowing multiple image to be selected
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Выберите фотографии"), PICK_IMAGE_MULTIPLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data

                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    int cout = data.getClipData().getItemCount();
                    //                mArrayUri = (ArrayList<Uri>) mArrayUriAbsolute.clone();
                    for (int i = 0; i < cout; i++) {
                        Uri imageurl = data.getClipData().getItemAt(i).getUri();
                        mArrayLocal.add(imageurl);
                    }
                    mArrayUri.clear();
                    mArrayUri.addAll(mArrayUriAbsolute);
                    mArrayUri.addAll(mArrayLocal);

                    addImagesInRecycler(mArrayUri);

                } else if (data.getData() != null) {
                    mArrayLocal.add(data.getData());
                    mArrayUri.clear();
                    mArrayUri.addAll(mArrayUriAbsolute);
                    mArrayUri.addAll(mArrayLocal);
                    addImagesInRecycler(mArrayUri);
                } else {
                    Toast.makeText(this, "Вы не выбрали ни одной картинки, ошибка", Toast.LENGTH_LONG).show();
                }
            } else {
                // show this if no image is selected
                Toast.makeText(this, "Вы не выбрали ни одной картинки", Toast.LENGTH_LONG).show();
            }
        }

        private void addImagesInRecycler(ArrayList<Uri> mArrayUri) {
            GalleryAdapter galleryAdapter = new GalleryAdapter(origContext, mArrayUri, this);

            gvGallery.setAdapter(galleryAdapter);

        }

        @Override
        public void onImageClick(int position) {
            if (position < mArrayUriAbsolute.size()) {
                if (mArrayUri.get(position) == mArrayUriAbsolute.get(position)) {
                    mDatabaseRef.child(type).child(idTask).child("images").child(String.valueOf(position)).removeValue();
                }
            } else {
                if (mArrayUri.get(position) == mArrayLocal.get(position - mArrayUriAbsolute.size())) {
                    mArrayLocal.remove(position - mArrayUriAbsolute.size());
                }
                mArrayUri.clear();
                mArrayUri.addAll(mArrayUriAbsolute);
                mArrayUri.addAll(mArrayLocal);
                addImagesInRecycler(mArrayUri);
            }
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_task, menu);
            return super.onCreateOptionsMenu(menu);
        }


        // Заглушка, работа с меню
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.menu_task_clear) {
                // Toast.makeText(getApplicationContext(), "Добавить примечание", Toast.LENGTH_SHORT).show();
                //            startActivity(new Intent(this, SendingActivity.class));
                mArrayUri.clear();
                mArrayLocal.clear();
                for (Uri imageUri : mArrayUriAbsolute) {
                    FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(imageUri)).delete();
                }
                mArrayUriAbsolute = new ArrayList<>();
                mDatabaseRef.child(type).child(idTask).child("images").removeValue();
                addImagesInRecycler(mArrayUri);
            } else if (id == R.id.menu_task_send) {
                mArrayUriDone.clear();
                Intent intentSending = new Intent(this, SendingActivity.class);
                startActivity(intentSending);
                for (int i = mArrayUriAbsolute.size(); i < mArrayUri.size(); i++) {
                    uploadFiles(mArrayUri.get(i));
                }
            }


            return super.onOptionsItemSelected(item);
        }

        private String getFileExtension(Uri uri) {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }

        private void uploadFiles(Uri uri) {
            if (mArrayUri != null) {
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));


                fileReference.putFile(uri)
                        .addOnSuccessListener(taskSnapshot -> {
                            fileReference.getDownloadUrl().addOnSuccessListener(uriAbsolute -> {
                                mArrayUriDone.add(uriAbsolute.toString());
                                mArrayUriAbsolute.add(uriAbsolute);
                                if (mArrayUriAbsolute.size() == mArrayUri.size()) {
                                    ArrayList<String> data = new ArrayList<>();
                                    for (int i = 0; i < mArrayUriAbsolute.size(); i++) {
                                        data.add(String.valueOf(mArrayUriAbsolute.get(i)));
                                    }
                                    mDatabaseRef.child(type).child(idTask).child("images").setValue(data);
                                    if (type.equals("current")) {
                                        mDatabaseRef.child(type).child(idTask).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                mDatabaseRef.child("done").child(idTask).setValue(dataSnapshot.getValue());
                                                mDatabaseRef.child(type).child(idTask).removeValue();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    SendingActivity.activity.finish();
                                    Toast.makeText(this, "Успешно отправлено", Toast.LENGTH_LONG).show();
                                    this.finish();

                                }
                            });

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Что-то пошло не так: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mArrayUri = new ArrayList<>();
                            addImagesInRecycler(mArrayUri);
                        });
            } else {
                Toast.makeText(this, "Файл не выбран", Toast.LENGTH_LONG).show();
            }
        }

    }

    public static class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

        private Context ctx;
        private RoundedImageView ivGallery;
        ArrayList<Uri> mArrayUri;
        private OnImageListener mOnImageListener;

        public GalleryAdapter(Context ctx, ArrayList<Uri> mArrayUri, OnImageListener mmOnImageListener) {
            this.ctx = ctx;
            this.mArrayUri = mArrayUri;
            this.mOnImageListener = mmOnImageListener;
        }


        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_task_item, parent, false);
            return new PhotoViewHolder(itemView, mOnImageListener);
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryAdapter.PhotoViewHolder holder, int position) {
            //            ivGallery.setImageURI(Uri.parse(mArrayUri.get(position)));
            Picasso.get().load(mArrayUri.get(position)).error(R.drawable.img_error).into(ivGallery);
            ivGallery.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Удаление")
                        .setMessage("Вы уверены что хотите удалить это фото?")
                        .setPositiveButton("Да", (dialog, id) -> {
                            // Закрываем окно
                            mOnImageListener.onImageClick(position);
                            dialog.cancel();
                        }).setNegativeButton("Нет", (dialog, id) -> {
                            dialog.cancel();
                        });
                builder.create().show();
                return false;
            });
        }



        @Override
        public int getItemCount() {
            return mArrayUri.size();
        }


        public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            OnImageListener mmOnImageListener;
            public PhotoViewHolder(@NonNull View itemView, OnImageListener OnImageListener) {
                super(itemView);
                ivGallery = (RoundedImageView) itemView.findViewById(R.id.photo);
                mmOnImageListener = OnImageListener;

                //            ivGallery.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                mOnImageListener.onImageClick(getAdapterPosition());
            }
        }

        public interface OnImageListener{
            void onImageClick(int position);
        }
    }

    public static class AddTaskActivity extends AppCompatActivity {

        EditText name, address, addressLink;
        Button addButton;

        String companyName;

        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference mDatabaseReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_task);

            mFirebaseDatabase = FirebaseDatabase.getInstance();
            //получаем ссылку для работы с базой данных
            companyName = getSharedPreferences("data", Context.MODE_PRIVATE).getString("companyName", "");
            mDatabaseReference = mFirebaseDatabase.getReference("companies/" + companyName + "/tasks/current");



            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle("Добавление задания");
                setSupportActionBar(toolbar);
            }

            name = findViewById(R.id.name);
            address = findViewById(R.id.address);
            addressLink = findViewById(R.id.addressLink);
            addButton = findViewById(R.id.addButton);

        }


        public void addButtonClickHandler(View view) {
            // Обрабатываем нажатие на кнопку
            String nameFinal = name.getText().toString();
            String id = nameFinal + System.currentTimeMillis();
            String addressFinal = address.getText().toString();
            String addressLinkFinal = addressLink.getText().toString();

            if (!nameFinal.isEmpty() && !addressFinal.isEmpty()) {
                if (!addressLinkFinal.isEmpty()) {
                    mDatabaseReference.child(id).setValue(new Task(id, nameFinal, addressFinal, addressLinkFinal, null));
                } else {
                    mDatabaseReference.child(id).setValue(new Task(id, nameFinal, addressFinal, null, null));
                }
                finish();
            }

        }
    }

    public static class LoginActivity extends AppCompatActivity {

        EditText email, password;
        Button loginButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle("Мерчандайзер");
                setSupportActionBar(toolbar);

            }

            email = findViewById(R.id.login);
            password = findViewById(R.id.password);
            loginButton = findViewById(R.id.loginButton);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            // Нажатие на кнопку регистрации
            loginButton.setOnClickListener(v -> {
                if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    // если поля не пустые
                    if (isValid(email.getText().toString())) {
                        // Если почта верна
                        if (password.getText().toString().length() >= 6) {
                            //Если пароль больше 6 символов
                            loginButton.setClickable(false);
                            Toast.makeText(getApplicationContext(), "Проверяем...", Toast.LENGTH_SHORT).show();
                            //Войти
                            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
                                    //Зарегистрировать
                                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                FirebaseDatabase.getInstance().getReference("users").child(user.getEmail().replace("@", "").replace(".", "").toLowerCase()).child("company").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue(String.class) == null) {
                                                            FirebaseDatabase.getInstance().getReference("users").child(user.getEmail().replace("@", "").replace(".", "")).setValue(new User(null, true));
                                                        }
                                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                                                });

                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Что-то пошло не так: " + task.getException() + " \n " + task2.getException(), Toast.LENGTH_LONG).show();
                                            email.setText("");
                                            password.setText("");
                                        }
                                    });
                                }
                            });

                            loginButton.setClickable(true);
                        } else {
                            Toast.makeText(this, "Пароль должен быть больше 6 символов", Toast.LENGTH_LONG).show();
                            password.setText("");
                        }
                    } else {
                        Toast.makeText(this, "E-mail не вырный", Toast.LENGTH_LONG).show();
                        email.setText("");
                        password.setText("");
                    }
                } else {
                    Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show();
                }

            });
        }

        public static boolean isValid(String email) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            Pattern pat = Pattern.compile(emailRegex);
            if (email == null)
                return false;
            return pat.matcher(email).matches();
        }

    }

    public static class SendingActivity extends AppCompatActivity {

        public static Activity activity;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sending);
            activity = this;


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle(R.string.wait);
                setSupportActionBar(toolbar);
            }


        }

    }

