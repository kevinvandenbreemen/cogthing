package com.vandenbreemen.cogthing.render;

import com.vandenbreemen.jgdv.Canvas;
import com.vandenbreemen.jgdv.mvp.SystemModel;
import com.vandenbreemen.jgdv.mvp.SystemPresenter;

import javax.swing.*;
import java.awt.*;

public class SecondaryGridVisualizer extends JFrame  {

    private Canvas gridCanvas;
    private SystemPresenter<SystemModel> presenter;

    public SecondaryGridVisualizer(String title, SystemModel model) throws HeadlessException {
        super(title);
        setBounds(10,10, 600,600);

        presenter = new SystemPresenter<>(model);
        gridCanvas = new Canvas(600, 600);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("Center", gridCanvas);

        presenter.initialize(gridCanvas);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void updateMe() {
        gridCanvas.repaint();
    }

    public void display() {
        setVisible(true);
    }
}
