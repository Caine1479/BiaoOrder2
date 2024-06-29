//package com.biaoorder2.adapter.base;
//
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewbinding.ViewBinding;
//import java.util.List;
//
//public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {
//
//    private List<T> items;
//
//    public BaseAdapter(List<T> items) {
//        this.items = items;
//    }
//
//    @NonNull
//    @Override
//    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        return new BaseViewHolder<>();
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BaseViewHolder<> holder, int position) {
//        T item = items.get(position);
//        bind(holder.binding, item);
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    public static class BaseViewHolder<VB extends ViewBinding> extends RecyclerView.ViewHolder {
//        VB binding;
//
//        public BaseViewHolder(VB binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//    }
//
//    protected abstract VB createBinding(LayoutInflater inflater, ViewGroup parent);
//
//    protected abstract void bind(VB binding, T item);
//}
